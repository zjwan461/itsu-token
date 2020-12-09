package com.itsu.itsutoken.controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itsu.itsutoken.annotation.TableDesc;
import com.itsu.itsutoken.configuration.ItsuTokenProperties;
import com.itsu.itsutoken.configuration.Type;
import com.itsu.itsutoken.exception.TokenCheckException;
import com.itsu.itsutoken.table.RSATableSample;
import com.itsu.itsutoken.table.SimpleTableSample;
import com.itsu.itsutoken.util.ClassUtil;

import cn.hutool.core.annotation.AnnotationUtil;

@RequestMapping("/tokenData")
public class TokenListController extends SuperController {

	@javax.annotation.Resource
    private JdbcTemplate jdbcTemplate;

	@javax.annotation.Resource
    private ItsuTokenProperties properties;

    @GetMapping("/list")
    public Map<String, Object> listData() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        String tableName = "tb_sys_token";
        String sysName = "sys_name";
        if (properties.getType() == Type.SIMPLE) {
            tableName = AnnotationUtil.getAnnotationValue(SimpleTableSample.class, TableDesc.class);
            sysName = ClassUtil.getSysValue(SimpleTableSample.class);
            final String tokenValue = ClassUtil.getSimpleTokenValue(SimpleTableSample.class);
            final String sysNameStr = sysName;
            final String tableId = ClassUtil.getId(SimpleTableSample.class);
            List<SimpleTableSample> list = jdbcTemplate.execute("select * from " + tableName,
                    new PreparedStatementCallback<List<SimpleTableSample>>() {

                        @Override
                        public List<SimpleTableSample> doInPreparedStatement(PreparedStatement ps)
                                throws SQLException, DataAccessException {
                            List<SimpleTableSample> list = new ArrayList<>();
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                SimpleTableSample tableSample = new SimpleTableSample();
                                tableSample.setId(rs.getString(tableId));
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
            final String tableId = ClassUtil.getId(RSATableSample.class);
            List<RSATableSample> list = jdbcTemplate.execute("select * from " + tableName,
                    new PreparedStatementCallback<List<RSATableSample>>() {

                        @Override
                        public List<RSATableSample> doInPreparedStatement(PreparedStatement ps)
                                throws SQLException, DataAccessException {
                            List<RSATableSample> list = new ArrayList<>();
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                RSATableSample rsaTableSample = new RSATableSample();
                                rsaTableSample.setId(rs.getString(tableId));
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

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable("id") @NonNull String id) throws Exception {
        // jdbcTemplate.update("delete from ", args)
        String tableName = "tb_sys_token";
        String idName = "id";
        if (properties.getType() == Type.SIMPLE) {
            tableName = AnnotationUtil.getAnnotationValue(SimpleTableSample.class, TableDesc.class);
            idName = ClassUtil.getId(SimpleTableSample.class);
        } else if (properties.getType() == Type.RSA) {
            tableName = AnnotationUtil.getAnnotationValue(RSATableSample.class, TableDesc.class);
            idName = ClassUtil.getId(RSATableSample.class);
        }
        jdbcTemplate.update("delete from " + tableName + " where " + idName + " = ? ", id);
        return "success";
    }

    @DeleteMapping("/all")
    public String deleteAll() {
        String tableName = "tb_sys_token";
        if (properties.getType() == Type.SIMPLE) {
            tableName = AnnotationUtil.getAnnotationValue(SimpleTableSample.class, TableDesc.class);
        } else if (properties.getType() == Type.RSA) {
            tableName = AnnotationUtil.getAnnotationValue(RSATableSample.class, TableDesc.class);
        }
        jdbcTemplate.update("delete from " + tableName);
        return "success";
    }
}