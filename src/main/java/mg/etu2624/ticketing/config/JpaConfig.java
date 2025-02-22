package mg.etu2624.ticketing.config;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.jpa.HibernatePersistenceProvider;

import jakarta.persistence.EntityManagerFactory;
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

    @Value("${hibernate.c3p0.max_size}")	
    private String poolSize = "20"; // Taille max du pool

    @Value("${hibernate.c3p0.min_size}")
    private String minPoolSize = "5"; // Taille min du pool
    
    @Value("${hibernate.c3p0.timeout}")
    private String connectionTimeout = "300"; // Timeout en secondes
    
    @Value("${hibernate.c3p0.max_statements}")
    private String maxStatements = "50"; // Nombre max de statements mis en cache
    
    @Value("${hibernate.c3p0.idle_test_period}")
    private String idleTestPeriod = "300"; // Intervalle de test des connexions inactives
    
    @Bean
    public EntityManagerFactory createEntityManagerFactory() {
        if (jdbcDriver == null || jdbcUrl == null || jdbcUser == null || jdbcPassword == null ||
            hibernateDialect == null || hbm2ddlAuto == null || showSql == null) {
            throw new RuntimeException("One or more JPA properties are not set");
        }

        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.driver", jdbcDriver);
        props.put("jakarta.persistence.jdbc.url", jdbcUrl);
        props.put("jakarta.persistence.jdbc.user", jdbcUser);
        props.put("jakarta.persistence.jdbc.password", jdbcPassword);

        props.put("hibernate.dialect", hibernateDialect);
        props.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
        props.put("hibernate.show_sql", showSql);

        // Configuration du pool de connexion avec valeurs par défaut
        props.put("hibernate.c3p0.min_size", minPoolSize);
        props.put("hibernate.c3p0.max_size", poolSize);
        props.put("hibernate.c3p0.timeout", connectionTimeout);
        props.put("hibernate.c3p0.max_statements", maxStatements);
        props.put("hibernate.c3p0.idle_test_period", idleTestPeriod);

        HibernatePersistenceProvider provider = new HibernatePersistenceProvider();
        return provider.createEntityManagerFactory("ticketing-persistence-unit", props);
    }
}