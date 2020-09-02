package com.itsu.itsutoken.configuration;

import com.itsu.itsutoken.table.SimpleTableSample;
import com.itsu.itsutoken.table.TableSample;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "itsu-token")
@Component
public class ItsuTokenProperties {

    private static Type typeName;

    private boolean enable;

    private Class<? extends TableSample> tableClass = SimpleTableSample.class;

    private Type type;

    private Init init;

    private WebRegister webRegister;

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

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public WebRegister getWebRegister() {
        return webRegister;
    }

    public void setWebRegister(WebRegister webRegister) {
        this.webRegister = webRegister;
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
            if (StringUtils.hasText(schemaLocation)) {
                return schemaLocation;
            } else {
                if (ItsuTokenProperties.typeName == Type.SIMPLE) {
                    this.schemaLocation = "classpath:schema/simpleSchema.sql";
                } else if (ItsuTokenProperties.typeName == Type.RSA) {
                    this.schemaLocation = "classpath:schema/rsaSchema.sql";
                }
            }
            return schemaLocation;
        }

        public void setSchemaLocation(String schemaLocation) {
            this.schemaLocation = schemaLocation;
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

    public static class WebRegister {
        private boolean enable;
        private String registerUrl = "registerToken";
        private String tokenListUrl = "tokenList";
        private String user = "admin";
        private String password = "password";

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getRegisterUrl() {
            return registerUrl;
        }

        public void setRegisterUrl(String registerUrl) {
            this.registerUrl = registerUrl;
        }

        public String getTokenListUrl() {
            return tokenListUrl;
        }

        public void setTokenListUrl(String tokenListUrl) {
            this.tokenListUrl = tokenListUrl;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }

}