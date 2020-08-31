package com.itsu.itsutoken.configuration;

import com.itsu.itsutoken.table.SimpleTableSample;
import com.itsu.itsutoken.table.TableSample;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "itsu-token")
public class ItsuTokenProperties {

    private static Type typeName;

    private boolean enable;

    private Class<? extends TableSample> tableClass = SimpleTableSample.class;

    private Type type;

    private Init init;

    private boolean webRegister;

    private System system;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
        typeName = type;
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

    public boolean isWebRegister() {
        return webRegister;
    }

    public void setWebRegister(boolean webRegister) {
        this.webRegister = webRegister;
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public static class Init {
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
            if (StringUtils.hasText(schemaLocation)) {
                this.schemaLocation = schemaLocation;
            } else {
                if (ItsuTokenProperties.typeName == Type.SIMPLE) {
                    this.schemaLocation = "classpath:schema/simpleSchema.sql";
                } else if (ItsuTokenProperties.typeName == Type.RSA) {
                    this.schemaLocation = "classpath:schema/rsaSchema.sql";
                }
            }
        }

    }

    public static class System {
        private boolean encryptBase64;

        public boolean isEncryptBase64() {
            return encryptBase64;
        }

        public void setEncryptBase64(boolean encryptBase64) {
            this.encryptBase64 = encryptBase64;
        }

    }

}