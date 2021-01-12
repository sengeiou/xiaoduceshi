package ai.qiwu.com.xiaoduhome.common.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @author 苗权威
 * @dateTime 19-8-22 下午2:23
 */
@Configuration
public class DataSourceConfig {

//    @Primary
//    @Bean(name = "primaryDatasourceProperties")
//    @Qualifier("primaryDatasourceProperties")
//    @ConfigurationProperties(prefix = "spring.datasource.primary")
//    public DataSourceProperties primaryDatasourceProperties() {
//        return new DataSourceProperties();
//    }

    @Bean("primaryDataSource")
    @Primary
    @Qualifier("primaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setConnectionTimeout(60000);
        dataSource.setMaxLifetime(60000);
        dataSource.setMaximumPoolSize(1000);
        dataSource.setReadOnly(false);
        dataSource.setIdleTimeout(60000);
        dataSource.setValidationTimeout(3000);
        dataSource.setMinimumIdle(10);
        dataSource.setPoolName("jiaoyoudb");
        return dataSource;
    }

    @Bean("secondaryDataSource")
    @Qualifier("secondaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource secondaryDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setConnectionTimeout(60000);
        dataSource.setMaxLifetime(60000);
        dataSource.setMaximumPoolSize(1000);
        dataSource.setReadOnly(false);
        dataSource.setIdleTimeout(60000);
        dataSource.setValidationTimeout(3000);
        dataSource.setMinimumIdle(10);
        dataSource.setPoolName("audiobox");
        return dataSource;
    }
}
