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

public class TicketingPersistenceUnitInfo implements PersistenceUnitInfo {

    private final String persistenceUnitName;
    private final Properties properties;
    private final URL persistenceUnitRootUrl;

    public TicketingPersistenceUnitInfo(String persistenceUnitName, Properties properties, URL persistenceUnitRootUrl) {
        this.persistenceUnitName = persistenceUnitName;
        this.properties = properties;
        this.persistenceUnitRootUrl = persistenceUnitRootUrl;
    }

    @Override
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    @Override
    public String getPersistenceProviderClassName() {
        return "org.hibernate.jpa.HibernatePersistenceProvider";
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return PersistenceUnitTransactionType.RESOURCE_LOCAL;
    }

    @Override
    public DataSource getJtaDataSource() {
        return null;
    }

    @Override
    public DataSource getNonJtaDataSource() {
        return null;
    }

    @Override
    public List<String> getMappingFileNames() {
        return Collections.emptyList();
    }

    @Override
    public List<URL> getJarFileUrls() {
        return Collections.emptyList();
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        return persistenceUnitRootUrl;
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
    public String getPersistenceXMLSchemaVersion() {
        return "2.2";
    }

    @Override
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void addTransformer(ClassTransformer transformer) {
        // Non implémenté
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        return null;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }
}