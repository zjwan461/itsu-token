package com.itsu.itsutoken.table;

import com.itsu.itsutoken.annotation.PrivateKey;
import com.itsu.itsutoken.annotation.PublicKey;
import com.itsu.itsutoken.annotation.SysName;
import com.itsu.itsutoken.annotation.TableDesc;

@TableDesc
public class RSATableSample implements TableSample {

    private String id;

    @SysName
    private String system_name;

    @PrivateKey
    private String private_key;

    @PublicKey
    private String public_key;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSystem_name() {
        return system_name;
    }

    public void setSystem_name(String system_name) {
        this.system_name = system_name;
    }

    public String getPrivate_key() {
        return private_key;
    }

    public void setPrivate_key(String private_key) {
        this.private_key = private_key;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }
}