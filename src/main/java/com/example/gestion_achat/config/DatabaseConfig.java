package com.example.gestion_achat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class DatabaseConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public DataSourceInitializer dataSourceInitializer() {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    private ResourceDatabasePopulator databasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();

        // Charge automatiquement tous les scripts SQL dans l'ordre
        Resource[] resources = getMigrationResources();
        populator.addScripts(resources);
        populator.setSeparator(";"); // Délimiteur des requêtes
        populator.setContinueOnError(true); // Continue même en cas d'erreur

        return populator;
    }

    private Resource[] getMigrationResources() {
        try {
            return new PathMatchingResourcePatternResolver()
                    .getResources("classpath:db/migration/*.sql");
        } catch (IOException e) {
            throw new RuntimeException("Erreur chargement scripts SQL", e);
        }
    }
}