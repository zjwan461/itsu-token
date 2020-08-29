package com.itsu.itsutoken.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

public class DataSourceInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceInitializer.class);

    private final DataSource dataSource;

    private final DataSourceProperties properties;

    private final ResourceLoader resourceLoader;


    DataSourceInitializer(DataSource dataSource, DataSourceProperties properties, ResourceLoader resourceLoader) {
        this.dataSource = dataSource;
        this.properties = properties;
        this.resourceLoader = (resourceLoader != null) ? resourceLoader : new DefaultResourceLoader(null);
    }


    DataSourceInitializer(DataSource dataSource, DataSourceProperties properties) {
        this(dataSource, properties, null);
    }


    private void runScripts(List<Resource> resources, String username, String password) {
        if (resources.isEmpty()) {
            return;
        }
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setContinueOnError(this.properties.isContinueOnError());
        populator.setSeparator(this.properties.getSeparator());
        if (this.properties.getSqlScriptEncoding() != null) {
            populator.setSqlScriptEncoding(this.properties.getSqlScriptEncoding().name());
        }
        for (Resource resource : resources) {
            populator.addScript(resource);
        }
        DataSource dataSource = this.dataSource;
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            dataSource = DataSourceBuilder.create(this.properties.getClassLoader())
                    .driverClassName(this.properties.determineDriverClassName()).url(this.properties.determineUrl())
                    .username(username).password(password).build();
        }
        DatabasePopulatorUtils.execute(populator, dataSource);
    }

    public void createSchema() {
        List<Resource> resources = properties.getSchema().stream().map(schema -> resourceLoader.getResource(schema)).collect(Collectors.toList());
        runScripts(resources, properties.getUsername(), properties.getPassword());
    }
}
