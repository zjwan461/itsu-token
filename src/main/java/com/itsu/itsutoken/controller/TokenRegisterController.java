package com.itsu.itsutoken.controller;

import java.util.HashMap;
import java.util.Map;

import com.itsu.itsutoken.annotation.TableDesc;
import com.itsu.itsutoken.configuration.ItsuTokenProperties;
import com.itsu.itsutoken.configuration.Type;
import com.itsu.itsutoken.exception.TokenCheckException;
import com.itsu.itsutoken.table.RSATableSample;
import com.itsu.itsutoken.table.SimpleTableSample;
import com.itsu.itsutoken.util.ClassUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.asymmetric.RSA;

@RestController
@RequestMapping("/token")
@ConditionalOnProperty(name = "itsu-token.web-register.enable", havingValue = "true", matchIfMissing = false)
public class TokenRegisterController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ItsuTokenProperties properties;

    @GetMapping("/generate/{system}")
    public Map<String, Object> generateToken(@PathVariable("system") @NonNull String system) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String tableName = "tb_sys_token";
        String sysName = "sys_name";
        if (properties.getType() == Type.SIMPLE) {
            tableName = AnnotationUtil.getAnnotationValue(SimpleTableSample.class, TableDesc.class);
            sysName = ClassUtil.getSysValue(SimpleTableSample.class);
            String token = ClassUtil.getSimpleTokenValue(SimpleTableSample.class);
            String generateToken = IdUtil.fastSimpleUUID();
            jdbcTemplate.update("insert into " + tableName + " (id," + sysName + "," + token + ") value (?,?,?)",
                    IdUtil.fastSimpleUUID(), system, generateToken);
            map.put("token", generateToken);
        } else if (properties.getType() == Type.RSA) {
            tableName = AnnotationUtil.getAnnotationValue(RSATableSample.class, TableDesc.class);
            sysName = ClassUtil.getSysValue(RSATableSample.class);
            String privateKey = ClassUtil.getPrivateKeyValue(RSATableSample.class);
            String publicKey = ClassUtil.getPublicKeyValue(RSATableSample.class);
            final RSA rsa = new RSA();
            String generatePrivateKey = rsa.getPrivateKeyBase64();
            String generatePublicKey = rsa.getPublicKeyBase64();
            jdbcTemplate.update(
                    "insert into " + tableName + " (id," + sysName + "," + privateKey + "," + publicKey
                            + ") value (?,?,?,?)",
                    IdUtil.fastSimpleUUID(), system, generatePrivateKey, generatePublicKey);
            map.put("publicKey", generatePublicKey);
            map.put("privateKey", generatePrivateKey);
        } else {
            throw new TokenCheckException("unknown Type ");
        }
        map.put("status", true);
        return map;
    }

    @GetMapping("/type")
    public Map<String, Object> getType() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", true);
        map.put("type", properties.getType().name().toLowerCase());
        return map;
    }

}