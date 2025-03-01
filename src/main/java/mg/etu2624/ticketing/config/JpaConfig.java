package mg.etu2624.ticketing.config;

import java.net.URL;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.jpa.HibernatePersistenceProvider;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import mg.itu.prom16.annotation.Bean;
import mg.itu.prom16.annotation.Configuration;
import mg.itu.prom16.annotation.Value;

@Configuration
public class JpaConfig {

    @Value("${jakarta.persistence.jdbc.driver}")
    private String jdbcDriver;

    @Value("${jakarta.persistence.jdbc.url}")
    private String jdbcUrl;

    @Value("${jakarta.persistence.jdbc.user}")
    private String jdbcUser;

    @Value("${jakarta.persistence.jdbc.password}")
    private String jdbcPassword;

    @Value("${hibernate.dialect}")
    private String hibernateDialect;

    @Value("${hibernate.hbm2ddl.auto}")
    private String hbm2ddlAuto;

    @Value("${hibernate.show_sql}")
    private String showSql;

    @Bean
    public EntityManagerFactory createEntityManagerFactory() {
        Properties jpaProperties = new Properties();
        
        // Configuration HikariCP révisée
        jpaProperties.put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
        jpaProperties.put("hibernate.hikari.connectionTimeout", "30000"); // Augmenté à 30s
        jpaProperties.put("hibernate.hikari.jdbcUrl", jdbcUrl);
        jpaProperties.put("hibernate.hikari.username", jdbcUser);
        jpaProperties.put("hibernate.hikari.password", jdbcPassword);
        jpaProperties.put("hibernate.connection.driver_class", jdbcDriver);
        
        // Paramètres de pool critiques
        jpaProperties.put("hibernate.hikari.maxLifetime", "1800000"); // 30 minutes
        jpaProperties.put("hibernate.hikari.idleTimeout", "600000"); // 10 minutes
        jpaProperties.put("hibernate.hikari.leakDetectionThreshold", "10000");
        
        // Configuration Hibernate
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"); // Forcé en dur temporairement
        jpaProperties.put("hibernate.hbm2ddl.auto", "none"); // Désactivé temporairement
        jpaProperties.put("hibernate.show_sql", showSql);

        // Chemin spécifique au package des entités
        String entityPackage = "mg/etu2624/ticketing/model"; // Chemin relatif
        URL rootUrl = Thread.currentThread()
                        .getContextClassLoader()
                        .getResource(entityPackage);
        
        if(rootUrl == null) {
            throw new IllegalStateException("Package des entités introuvable: " + entityPackage);
        }

        // Création de l'EMF
        TicketingPersistenceUnitInfo persistenceUnitInfo = new TicketingPersistenceUnitInfo(
            "TicketingPU", 
            jpaProperties,
            rootUrl
        );
        
        return new HibernatePersistenceProvider()
            .createContainerEntityManagerFactory(persistenceUnitInfo, jpaProperties);
    }
    public static String getSqlMessage(PersistenceException e) {
        Throwable rootCause = e;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        
        String errorMessage = "Erreur inconnue";
        if (rootCause instanceof SQLException) {
            String fullMessage = rootCause.getMessage();
            // Extraction du message métier
            errorMessage = fullMessage.replaceAll("(?s).*ERREUR:\\s*(.*?)\\s*Où.*", "$1");
        }
        
        return errorMessage;
    }
}