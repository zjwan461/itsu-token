package com.itsu.itsutoken.configuration;

import com.itsu.itsutoken.table.SimpleTableSample;
import com.itsu.itsutoken.table.TableSample;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "itsu-token")
public class ItsuTokenProperties {

    private boolean enable;

    private String tableName = "tb_token_info";

    private Class<? extends TableSample> tableClass = SimpleTableSample.class;

    private Type type;

    private Init init;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Class<? extends TableSample> getTableClass() {
        return tableClass;
    }

    public void setTableClass(Class<? extends TableSample> tableClass) {
        this.tableClass = tableClass;
    }

    public Init getInit() {
        return init;
    }

    public void setInit(Init init) {
        this.init = init;
    }

    public enum Type {
        SIMPLE, RSA;
    }

    class Init {
        private boolean autoCreateTable;
        private String schemaLocation;

        public boolean isAutoCreateTable() {
            return autoCreateTable;
        }

        public void setAutoCreateTable(boolean autoCreateTable) {
            this.autoCreateTable = autoCreateTable;
        }

        public String getSchemaLocation() {
            return schemaLocation;
        }

        public void setSchemaLocation(String schemaLocation) {
            this.schemaLocation = schemaLocation;
        }

    }

}