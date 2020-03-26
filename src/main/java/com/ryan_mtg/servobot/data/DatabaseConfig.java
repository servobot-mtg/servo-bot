package com.ryan_mtg.servobot.data;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import com.ryan_mtg.servobot.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ComponentScan
@EnableJpaRepositories
public class DatabaseConfig {
    private static Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    @Value("${database.server}")
    private String server;

    @Bean
    protected LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource());
        factory.setPackagesToScan(DatabaseConfig.class.getPackage().getName());

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaProperties(additionalProperties());

        return factory;
    }

    @Bean
    protected DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        final String connectionUrlPattern = "jdbc:mysql://%s:3306/botdb?useUnicode=yes&characterEncoding=UTF-8";
        String url = String.format(connectionUrlPattern, server);
        if (Application.isTesting()) {
            url = String.format(connectionUrlPattern, "localhost");
        }
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    protected PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") final EntityManagerFactory factory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(factory);

        return transactionManager;
    }

    @Bean
    protected PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

        return properties;
    }
}
