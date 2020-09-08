package com.itsu.itsutoken.controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itsu.itsutoken.annotation.TableDesc;
import com.itsu.itsutoken.configuration.ItsuTokenProperties;
import com.itsu.itsutoken.configuration.Type;
import com.itsu.itsutoken.exception.TokenCheckException;
import com.itsu.itsutoken.table.RSATableSample;
import com.itsu.itsutoken.table.SimpleTableSample;
import com.itsu.itsutoken.util.ClassUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.annotation.AnnotationUtil;

@RestController
@RequestMapping("/tokenData")
public class TokenListController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ItsuTokenProperties properties;

    @GetMapping("/list")
    public Map<String, Object> listData() throws Exception {
        Map<String, Object> map = new HashMap<>();
        String tableName = "tb_sys_token";
        String sysName = "sys_name";
        if (properties.getType() == Type.SIMPLE) {
            tableName = AnnotationUtil.getAnnotationValue(SimpleTableSample.class, TableDesc.class);
            sysName = ClassUtil.getSysValue(SimpleTableSample.class);
            final String tokenValue = ClassUtil.getSimpleTokenValue(SimpleTableSample.class);
            final String sysNameStr = sysName;
            List<SimpleTableSample> list = jdbcTemplate.execute("select * from " + tableName,
                    new PreparedStatementCallback<List<SimpleTableSample>>() {

                        @Override
                        public List<SimpleTableSample> doInPreparedStatement(PreparedStatement ps)
                                throws SQLException, DataAccessException {
                            List<SimpleTableSample> list = new ArrayList<>();
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                SimpleTableSample tableSample = new SimpleTableSample();
                                tableSample.setId(rs.getString("id"));
                                tableSample.setSystem_name(rs.getString(sysNameStr));
                                tableSample.setToken(rs.getString(tokenValue));
                                list.add(tableSample);
                            }
                            return list;
                        }
                    });
            map.put("data", list);
            map.put("type", Type.SIMPLE.name().toLowerCase());
        } else if (properties.getType() == Type.RSA) {
            tableName = AnnotationUtil.getAnnotationValue(RSATableSample.class, TableDesc.class);
            sysName = ClassUtil.getSysValue(RSATableSample.class);
            final String privateKeyValue = ClassUtil.getPrivateKeyValue(RSATableSample.class);
            final String publicKeyValue = ClassUtil.getPublicKeyValue(RSATableSample.class);
            final String sysNameStr = sysName;
            List<RSATableSample> list = jdbcTemplate.execute("select * from " + tableName,
                    new PreparedStatementCallback<List<RSATableSample>>() {

                        @Override
                        public List<RSATableSample> doInPreparedStatement(PreparedStatement ps)
                                throws SQLException, DataAccessException {
                            List<RSATableSample> list = new ArrayList<>();
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                RSATableSample rsaTableSample = new RSATableSample();
                                rsaTableSample.setId(rs.getString("id"));
                                rsaTableSample.setSystem_name(rs.getString(sysNameStr));
                                rsaTableSample.setPrivate_key(rs.getString(privateKeyValue));
                                rsaTableSample.setPublic_key(rs.getString(publicKeyValue));
                                list.add(rsaTableSample);
                            }
                            return list;
                        }
                    });
            map.put("data", list);
            map.put("type", Type.RSA.name().toLowerCase());
        } else {
            throw new TokenCheckException("Unsupported Type");
        }

        return map;

    }
}