package ai.qiwu.com.xiaoduhome.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/**
 * @author 苗权威
 * @dateTime 19-8-22 下午3:21
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactorySecondary",
            transactionManagerRef = "transactionManagerSecondary",
            basePackages = {"ai.qiwu.com.xiaoduhome.repository.secondary"})
public class SecondaryConfig {
    @Autowired
    @Qualifier("secondaryDataSource")
    private DataSource secondaryDataSource;

    @Bean(name = "entityManagerFactorySecondary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder,
                                                                              JpaProperties jpaProperties,
                                                                              HibernateProperties hibernateProperties) {
        Map<String, Object> properties = hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(),
                new HibernateSettings());
        return builder
                .dataSource(secondaryDataSource)
                .properties(properties)
                .packages("ai.qiwu.com.xiaoduhome.entity.secondary")
                .persistenceUnit("secondaryPersistenceUnit")
                .build();
    }

    @Bean(name = "entityManagerSecondary")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder,
                                       JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
        return entityManagerFactoryPrimary(builder, jpaProperties,hibernateProperties).getObject().createEntityManager();
    }

    @Bean(name = "transactionManagerSecondary")
    public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder,
                                                         JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
        return new JpaTransactionManager(entityManagerFactoryPrimary(builder, jpaProperties, hibernateProperties).getObject());
    }
}
