package mg.etu2624.ticketing.config;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;

public class TicketingPersistenceUnitInfo implements PersistenceUnitInfo {

    private final String persistenceUnitName;
    private final Properties properties;
    private final DataSource dataSource;

    public TicketingPersistenceUnitInfo(String persistenceUnitName, Properties properties) {
        this.persistenceUnitName = persistenceUnitName;
        this.properties = properties;
        this.dataSource = createDataSource();
    }

    private DataSource createDataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();
        
        // Configurer la source de données avec les propriétés du fichier de configuration
        basicDataSource.setDriverClassName(properties.getProperty("jakarta.persistence.jdbc.driver"));
        basicDataSource.setUrl(properties.getProperty("jakarta.persistence.jdbc.url"));
        basicDataSource.setUsername(properties.getProperty("jakarta.persistence.jdbc.user"));
        basicDataSource.setPassword(properties.getProperty("jakarta.persistence.jdbc.password"));
        
        // Paramètres de pool
        basicDataSource.setMaxTotal(10);    // Nombre max de connexions
        basicDataSource.setMinIdle(2);      // Nombre minimum de connexions
        basicDataSource.setMaxIdle(5);      // Nombre maximum de connexions inactives
        basicDataSource.setTestOnBorrow(true);  // Teste la connexion avant de l'emprunter
        basicDataSource.setValidationQuery("SELECT 1"); // Requête de validation
        
        return basicDataSource;
    }

    @Override
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    @Override
    public String getPersistenceProviderClassName() {
        return HibernatePersistenceProvider.class.getName();
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return PersistenceUnitTransactionType.RESOURCE_LOCAL;
    }

    @Override
    public DataSource getJtaDataSource() {
        return dataSource; // Pas de gestion JTA
    }

    @Override
    public DataSource getNonJtaDataSource() {
        return dataSource; // Retourne la DataSource créée
    }

    @Override
    public List<String> getMappingFileNames() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getManagedClassNames() {
        return Collections.emptyList();
    }

    @Override
    public boolean excludeUnlistedClasses() {
        return false;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return SharedCacheMode.UNSPECIFIED;
    }

    @Override
    public ValidationMode getValidationMode() {
        return ValidationMode.AUTO;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return "2.1";
    }

    @Override
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void addTransformer(ClassTransformer transformer) {}

    @Override
    public ClassLoader getNewTempClassLoader() {
        return getClassLoader();
    }

    @Override
    public List<URL> getJarFileUrls() {
        return Collections.emptyList(); 
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        try {
            return getClass().getProtectionDomain().getCodeSource().getLocation();
        } catch (Exception e) {
            throw new RuntimeException("Impossible de déterminer l'URL racine de l'unité de persistance", e);
        }
    }
}
