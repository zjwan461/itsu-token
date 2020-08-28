package com.itsu.itsutoken.configuration;

import javax.sql.DataSource;

import com.itsu.itsutoken.checker.RSATokenChecker;
import com.itsu.itsutoken.checker.SimpleTokenChecker;
import com.itsu.itsutoken.checker.TokenChecker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableConfigurationProperties(ItsuTokenProperties.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnClass({ TokenChecker.class })
@ConditionalOnBean(DataSource.class)
public class ItsuTokenAutoConfiguration {

    @Autowired
    private ItsuTokenProperties properties;

    @Bean
    public TokenChecker tokenChecker(DataSource dataSource, DataSourceProperties dataSourceProperties) {
        TokenChecker tokenChecker = init(dataSource, dataSourceProperties);

        return tokenChecker;
    }

    public TokenChecker init(DataSource dataSource, DataSourceProperties dataSourceProperties) {
        TokenChecker tokenChecker = null;
        if (properties.getType() == ItsuTokenProperties.Type.SIMPLE) {
            tokenChecker = new SimpleTokenChecker();
        } else if (properties.getType() == ItsuTokenProperties.Type.RSA) {
            tokenChecker = new RSATokenChecker();
        }

        if (properties.getInit().isAutoCreateTable()) {
            String schemaLocation = properties.getInit().getSchemaLocation();
            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer(dataSource, dataSourceProperties);
        } else {

        }
        return tokenChecker;
    }

}