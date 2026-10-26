package edu.northwestern.at.utils.tools;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.ColumnOrderingStrategy;
import org.hibernate.dialect.temptable.TemporaryTableColumn;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Constraint;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UserDefinedObjectType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeclarationOrderColumnOrderingStrategy implements ColumnOrderingStrategy {

    @Override
    public List<Column> orderTableColumns(Table table, Metadata metadata) {
        if (table == null) {
            return Collections.emptyList();
        }
        if (metadata == null) {
            return new ArrayList<>(table.getColumns());
        }

        List<PersistentClass> pcs = new ArrayList<>();
        for (PersistentClass pc : metadata.getEntityBindings()) {
            if (pc.getTable() != null && table.equals(pc.getTable())) {
                pcs.add(pc);
            }
        }

        if (pcs.isEmpty()) {
            // Check if table is a collection / join table
            org.hibernate.mapping.Collection matchedColl = null;
            for (org.hibernate.mapping.Collection coll : metadata.getCollectionBindings()) {
                if (coll.getCollectionTable() != null && table.equals(coll.getCollectionTable())) {
                    matchedColl = coll;
                    if (!coll.isInverse()) {
                        break;
                    }
                }
            }
            if (matchedColl != null) {
                return orderCollectionColumns(table, matchedColl);
            }
            return new ArrayList<>(table.getColumns());
        }

        // Sort PersistentClasses so that base classes appear before subclasses.
        pcs.sort((pc1, pc2) -> {
            Class<?> c1 = pc1.getMappedClass();
            Class<?> c2 = pc2.getMappedClass();
            if (c1.isAssignableFrom(c2)) return -1;
            if (c2.isAssignableFrom(c1)) return 1;
            return c1.getName().compareTo(c2.getName());
        });

        PersistentClass rootPc = pcs.get(0);
        List<Column> ordered = new ArrayList<>();
        Set<Column> seen = new HashSet<>();

        // 1. Primary Key / Identifier columns (e.g. "id")
        if (rootPc.getIdentifierProperty() != null) {
            collectPropertyColumns(rootPc.getIdentifierProperty(), ordered, seen);
        } else if (rootPc.getIdentifier() != null) {
            if (rootPc.getIdentifier() instanceof Component) {
                collectComponentColumns((Component) rootPc.getIdentifier(), ordered, seen);
            } else {
                for (Column c : rootPc.getIdentifier().getColumns()) {
                    if (seen.add(c)) {
                        ordered.add(c);
                    }
                }
            }
        }

        // 2. Discriminator column (e.g. "type")
        if (rootPc.getDiscriminator() != null) {
            for (Column c : rootPc.getDiscriminator().getColumns()) {
                if (seen.add(c)) {
                    ordered.add(c);
                }
            }
        }

        // 3. Properties ordered by Java class declaration order
        Set<Class<?>> allEntityClasses = new HashSet<>();
        for (PersistentClass pc : pcs) {
            allEntityClasses.add(pc.getMappedClass());
        }

        for (PersistentClass pc : pcs) {
            Class<?> clazz = pc.getMappedClass();
            Map<String, Property> propMap = new HashMap<>();
            for (Property p : pc.getPropertyClosure()) {
                propMap.put(p.getName(), p);
            }

            // Inspect any non-entity superclasses up to Object, followed by clazz
            List<Class<?>> classesToInspect = new ArrayList<>();
            for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
                if (c != clazz && allEntityClasses.contains(c)) {
                    break;
                }
                classesToInspect.add(0, c);
            }

            for (Class<?> c : classesToInspect) {
                for (Field f : c.getDeclaredFields()) {
                    if (Modifier.isStatic(f.getModifiers()) || f.isSynthetic()) {
                        continue;
                    }
                    Property p = propMap.get(f.getName());
                    if (p != null) {
                        collectPropertyColumns(p, ordered, seen);
                    }
                }
            }

            // Fallback for declared properties not matched to fields
            for (Property p : pc.getDeclaredProperties()) {
                collectPropertyColumns(p, ordered, seen);
            }
        }

        // Append any remaining unmapped columns from the table
        for (Column c : table.getColumns()) {
            if (seen.add(c)) {
                ordered.add(c);
            }
        }

        return ordered;
    }

    @Override
    public List<Column> orderConstraintColumns(Constraint constraint, Metadata metadata) {
        if (constraint == null || constraint.getTable() == null) {
            return null;
        }
        List<Column> tableOrdered = orderTableColumns(constraint.getTable(), metadata);
        if (tableOrdered == null || tableOrdered.isEmpty()) {
            return null;
        }
        Map<Column, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < tableOrdered.size(); i++) {
            orderMap.put(tableOrdered.get(i), i);
        }
        List<Column> constraintColumns = new ArrayList<>(constraint.getColumns());
        constraintColumns.sort(Comparator.comparingInt(c -> orderMap.getOrDefault(c, Integer.MAX_VALUE)));
        return constraintColumns;
    }

    @Override
    public List<Column> orderUserDefinedTypeColumns(UserDefinedObjectType userDefinedType, Metadata metadata) {
        return null;
    }

    @Override
    public void orderTemporaryTableColumns(List<TemporaryTableColumn> temporaryTableColumns, Metadata metadata) {
    }

    private static void collectPropertyColumns(Property property, List<Column> ordered, Set<Column> seen) {
        if (property == null) {
            return;
        }
        if (property.getValue() instanceof Component) {
            collectComponentColumns((Component) property.getValue(), ordered, seen);
        } else {
            for (Column c : property.getColumns()) {
                if (seen.add(c)) {
                    ordered.add(c);
                }
            }
        }
    }

    private static void collectComponentColumns(Component comp, List<Column> ordered, Set<Column> seen) {
        Class<?> clazz = comp.getComponentClass();
        Map<String, Property> propMap = new HashMap<>();
        for (Property subP : comp.getProperties()) {
            propMap.put(subP.getName(), subP);
        }

        if (clazz != null) {
            List<Class<?>> compHierarchy = new ArrayList<>();
            for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
                compHierarchy.add(0, c);
            }
            for (Class<?> c : compHierarchy) {
                for (Field f : c.getDeclaredFields()) {
                    if (Modifier.isStatic(f.getModifiers()) || f.isSynthetic()) {
                        continue;
                    }
                    Property subP = propMap.get(f.getName());
                    if (subP != null) {
                        collectPropertyColumns(subP, ordered, seen);
                    }
                }
            }
        }

        // Fallback for any properties not matched to fields
        for (Property subP : comp.getProperties()) {
            collectPropertyColumns(subP, ordered, seen);
        }
    }

    private static List<Column> orderCollectionColumns(Table table, org.hibernate.mapping.Collection coll) {
        List<Column> ordered = new ArrayList<>();
        Set<Column> seen = new HashSet<>();

        // 1. Key columns (e.g. parent_id)
        if (coll.getKey() != null) {
            for (Column c : coll.getKey().getColumns()) {
                if (seen.add(c)) {
                    ordered.add(c);
                }
            }
        }

        // 2. Index columns (if indexed collection, e.g. child_index)
        if (coll instanceof IndexedCollection) {
            IndexedCollection ic = (IndexedCollection) coll;
            if (ic.getIndex() != null) {
                for (Column c : ic.getIndex().getColumns()) {
                    if (seen.add(c)) {
                        ordered.add(c);
                    }
                }
            }
        }

        // 3. Element columns (e.g. child_id)
        if (coll.getElement() != null) {
            if (coll.getElement() instanceof Component) {
                collectComponentColumns((Component) coll.getElement(), ordered, seen);
            } else {
                for (Column c : coll.getElement().getColumns()) {
                    if (seen.add(c)) {
                        ordered.add(c);
                    }
                }
            }
        }

        // Append any remaining columns
        for (Column c : table.getColumns()) {
            if (seen.add(c)) {
                ordered.add(c);
            }
        }

        return ordered;
    }
}
