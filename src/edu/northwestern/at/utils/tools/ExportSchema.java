package edu.northwestern.at.utils.tools;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import jakarta.persistence.Entity;
import jakarta.persistence.SchemaManager;

import java.util.Set;

public class ExportSchema {
    public static void main(String... args) {
        Options options = new Options();
        options.addOption(Option.builder("d").longOpt("dialect").required().hasArg().desc("Hibernate dialect").build());
        options.addOption(Option.builder("o").longOpt("output").required().hasArg().desc("Output file").build());
        options.addOption(Option.builder("e").longOpt("entities").required().hasArg().desc("Entities class path").build());
        options.addOption(Option.builder("p").longOpt("password").required().hasArg().desc("Hibernate connection password").build());
        options.addOption(Option.builder("hu").longOpt("url").required().hasArg().desc("Hibernate connection URL").build());
        options.addOption(Option.builder("j").longOpt("jdbc_driver").required().hasArg().desc("Hibernate JDBC driver").build());
        options.addOption(Option.builder("u").longOpt("username").required().hasArg().desc("Hibernate connection username").build());
        options.addOption(Option.builder("h").longOpt("help").desc("Print help").build());

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                showUsage(options);
            }
            String hibernateDialect = cmd.getOptionValue("d");
            String packages = cmd.getOptionValue("e");
            String outputFile = cmd.getOptionValue("o");
            String hibernateURL = cmd.getOptionValue("hu");
            String jdbcDriver = cmd.getOptionValue("j");
            String hibernateUsername = cmd.getOptionValue("u");
            String hibernatePassword = cmd.getOptionValue("p");
            new ExportSchema().generateSchema(hibernateDialect,
                                              packages,
                                              outputFile,
                                              hibernateURL,
                                              jdbcDriver,
                                              hibernateUsername,
                                              hibernatePassword);
        }
        catch (ParseException e) {
            System.err.println("Error generating schema: " + e.getMessage());
            showUsage(options);
        }
    }

    private void generateSchema(String hibernateDialect,
                                String packages,
                                String outputFile,
                                String hibernateURL,
                                String jdbcDriver,
                                String hibernateUsername,
                                String hibernatePassword) {

        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySetting(Environment.JAKARTA_JDBC_DRIVER, jdbcDriver)
                .applySetting(AvailableSettings.DIALECT, hibernateDialect)
                .applySetting(Environment.JAKARTA_JDBC_URL, hibernateURL)
                .applySetting(Environment.JAKARTA_JDBC_USER, hibernateUsername)
                .applySetting(Environment.JAKARTA_JDBC_PASSWORD, hibernatePassword)
                .applySetting("jakarta.persistence.schema-generation.database.action", "none")
                .applySetting("jakarta.persistence.schema-generation.scripts.action", "create")
                .applySetting("jakarta.persistence.schema-generation.scripts.create-target", outputFile)
                .applySetting(AvailableSettings.FORMAT_SQL, true)
                //.applySetting(AvailableSettings.COLUMN_ORDERING_STRATEGY, "default") // declaration_order is unavailable
                .build();

        Set<Class<?>> entities = scanForEntities(packages);
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        entities.forEach(metadataSources::addAnnotatedClass);
        Metadata metadata = metadataSources.buildMetadata();
        try (SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor) metadata.buildSessionFactory()) {
            SchemaManager schemaManager = sessionFactory.getSchemaManager();

            // We use validate() rather than create() here since we only want the output file.
            schemaManager.validate();
        }
        catch (Exception e) {
            System.err.println("Error in schemaManager.validate: " + e.getMessage());
        }
    }

    private Set<Class<?>> scanForEntities(String... packages) {
        Reflections reflections = new Reflections(
            new ConfigurationBuilder()
                    .forPackages(packages)
        );
       return reflections.getTypesAnnotatedWith(Entity.class);
    }

    private static void showUsage (Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ExportSchema", options);
        System.exit(0);
    }
}