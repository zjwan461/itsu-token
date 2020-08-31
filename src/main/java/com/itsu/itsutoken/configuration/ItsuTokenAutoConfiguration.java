package com.itsu.itsutoken.configuration;

import java.util.Arrays;

import javax.sql.DataSource;

import com.itsu.itsutoken.checker.TokenChecker;
import com.itsu.itsutoken.table.TableSample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import cn.hutool.core.collection.CollectionUtil;

@Configuration
@EnableConfigurationProperties(ItsuTokenProperties.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnClass({ TokenChecker.class })
@ConditionalOnProperty(name = "itsu-token.enable", havingValue = "true", matchIfMissing = true)
public class ItsuTokenAutoConfiguration {

    @Autowired
    private ItsuTokenProperties properties;

    @Bean
    public TokenChecker<? extends TableSample> tokenChecker(DataSource dataSource,
            DataSourceProperties dataSourceProperties) {
        TokenChecker<? extends TableSample> tokenChecker = properties.getType().generateTokenChecher();

        if (properties.getInit().isAutoCreateTable()) {
            if (CollectionUtil.isEmpty(dataSourceProperties.getSchema())) {
                String schemaLocation = properties.getInit().getSchemaLocation();
                dataSourceProperties.setSchema(Arrays.asList(schemaLocation));
            }
            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer(dataSource, dataSourceProperties);
            dataSourceInitializer.createSchema();
        }

        return tokenChecker;
    }

}