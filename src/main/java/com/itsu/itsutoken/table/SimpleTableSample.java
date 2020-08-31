package com.itsu.itsutoken.table;

import com.itsu.itsutoken.annotation.SimpleToken;
import com.itsu.itsutoken.annotation.SysName;
import com.itsu.itsutoken.annotation.TableDesc;

@TableDesc
public class SimpleTableSample implements TableSample {

    private String id;

    @SimpleToken
    private String token;

    @SysName
    private String system_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSystem_name() {
        return system_name;
    }

    public void setSystem_name(String system_name) {
        this.system_name = system_name;
    }

}