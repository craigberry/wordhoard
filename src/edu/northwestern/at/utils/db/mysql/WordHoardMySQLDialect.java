package edu.northwestern.at.utils.db.mysql;

import org.hibernate.dialect.MySQLDialect;
import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.query.sqm.function.PatternBasedSqmFunctionDescriptor;
import org.hibernate.query.sqm.produce.function.internal.PatternRenderer;
import org.hibernate.query.sqm.produce.function.StandardArgumentsValidators;
import org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.spi.TypeConfiguration;

public class WordHoardMySQLDialect extends MySQLDialect {
    /**
     * Register a custom function to provide regular expression matching in HQL.
     */
    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        TypeConfiguration typeConfiguration = functionContributions.getTypeConfiguration();

        PatternRenderer renderer = new PatternRenderer("?1 RLIKE ?2");

        PatternBasedSqmFunctionDescriptor descriptor = new PatternBasedSqmFunctionDescriptor(
            renderer,
            StandardArgumentsValidators.exactly(2),
            StandardFunctionReturnTypeResolvers.invariant(typeConfiguration.getBasicTypeRegistry().resolve(StandardBasicTypes.BOOLEAN)),
            null, null, null, null
        );
        functionContributions.getFunctionRegistry().register("REGEXP_LIKE", descriptor);
    }
}

/*
 * <p>
 * Copyright &copy; 2022 Martin Mueller and Craig A. Berry.
 * </p>
 * <p>
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * </p>
 * <p>
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more
 * details.
 * </p>
 * <p>
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA.
 * </p>
 */