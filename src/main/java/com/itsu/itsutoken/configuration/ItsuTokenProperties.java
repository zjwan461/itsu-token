package com.itsu.itsutoken.configuration;

import com.itsu.itsutoken.checker.RSATokenChecker;
import com.itsu.itsutoken.checker.SimpleTokenChecker;
import com.itsu.itsutoken.checker.TokenChecker;
import com.itsu.itsutoken.table.RSATableSample;
import com.itsu.itsutoken.table.SimpleTableSample;
import com.itsu.itsutoken.table.TableSample;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "itsu-token")
public class ItsuTokenProperties {

    private static Type typeName;

    private boolean enable;

    private String tableName = "tb_sys_token";

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

    public enum Type {
        SIMPLE {
            @Override
            public TokenChecker<? extends TableSample> generateTokenChecher() {
                return new SimpleTokenChecker(new SimpleTableSample());
            }
        }, RSA {
            @Override
            public TokenChecker<? extends TableSample> generateTokenChecher() {
                return new RSATokenChecker(new RSATableSample());
            }
        };

        public abstract TokenChecker<? extends TableSample> generateTokenChecher();
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

}