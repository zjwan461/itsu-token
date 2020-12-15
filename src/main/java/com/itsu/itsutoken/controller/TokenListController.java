package com.itsu.itsutoken.controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.itsu.itsutoken.annotation.TableDesc;
import com.itsu.itsutoken.configuration.ItsuTokenProperties;
import com.itsu.itsutoken.configuration.Type;
import com.itsu.itsutoken.exception.TokenCheckException;
import com.itsu.itsutoken.table.RSATableSample;
import com.itsu.itsutoken.table.SimpleTableSample;
import com.itsu.itsutoken.table.TableSample;
import com.itsu.itsutoken.util.ClassUtil;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.extra.spring.SpringUtil;

public class TokenListController extends SuperController {

	private static final Logger log = LoggerFactory.getLogger(TokenListController.class);

	@javax.annotation.Resource
	private JdbcTemplate jdbcTemplate;

	@javax.annotation.Resource
	private ItsuTokenProperties properties;

	@GetMapping("/tokenData/list")
	public Map<String, Object> listData() throws Exception {
		Map<String, Object> map = new LinkedHashMap<>();
		String tableName = "tb_sys_token";
		String sysName = "sys_name";
		Class<? extends TableSample> tableSampleClass = null;
		try {
			TableSample tableSample = SpringUtil.getBean(TableSample.class);
			if (log.isDebugEnabled()) {
				log.debug("user set custom tableSample [" + tableSample.getClass().getName() + "]");
			}

			try {
				Integer value = (Integer) tableSample.getClass().getMethod("tip").invoke(tableSample);
				if (value != 0) {
					throw new TokenCheckException(
							"if you set custom-schema to true you need provide a tableSample Class which implements com.itsu.itsutoken.table.TableSample and inject into Spring application context");
				}
				tableSampleClass = tableSample.getClass();
			} catch (Exception e) {
				throw new TokenCheckException(e);
			}
		} catch (NoSuchBeanDefinitionException e) {
			if (log.isDebugEnabled()) {
				log.debug("user do not set set custom tableSample, will use default");
			}
		}
		
		if (properties.getType() == Type.SIMPLE) {
			if (tableSampleClass == null)
				tableSampleClass = SimpleTableSample.class;
			tableName = AnnotationUtil.getAnnotationValue(tableSampleClass, TableDesc.class);
			sysName = ClassUtil.getSysValue(tableSampleClass);
			final String tokenValue = ClassUtil.getSimpleTokenValue(tableSampleClass);
			final String sysNameStr = sysName;
			final String tableId = ClassUtil.getId(tableSampleClass);
			List list = jdbcTemplate.execute("select * from " + tableName,
					new PreparedStatementCallback<List>() {

						@Override
						public List doInPreparedStatement(PreparedStatement ps)
								throws SQLException, DataAccessException {
							List list = new ArrayList<>();
							ResultSet rs = ps.executeQuery();
							while (rs.next()) {
//								SimpleTableSample tableSample = new SimpleTableSample();
//								tableSample.setId(rs.getString(tableId));
//								tableSample.setSystem_name(rs.getString(sysNameStr));
//								tableSample.setToken(rs.getString(tokenValue));
//								list.add(tableSample);
								Map map = new HashMap<>();
								map.put("id", rs.getString(tableId));
								map.put("system_name", rs.getString(sysNameStr));
								map.put("token", rs.getString(tokenValue));
								list.add(map);
							}
							return list;
						}
					});
			map.put("data", list);
			map.put("type", Type.SIMPLE.name().toLowerCase());
		} else if (properties.getType() == Type.RSA) {
			if (tableSampleClass == null)
				tableSampleClass = RSATableSample.class;
			tableName = AnnotationUtil.getAnnotationValue(tableSampleClass, TableDesc.class);
			sysName = ClassUtil.getSysValue(tableSampleClass);
			final String privateKeyValue = ClassUtil.getPrivateKeyValue(tableSampleClass);
			final String publicKeyValue = ClassUtil.getPublicKeyValue(tableSampleClass);
			final String sysNameStr = sysName;
			final String tableId = ClassUtil.getId(RSATableSample.class);
			List list = jdbcTemplate.execute("select * from " + tableName,
					new PreparedStatementCallback<List>() {

						@Override
						public List doInPreparedStatement(PreparedStatement ps)
								throws SQLException, DataAccessException {
							List list = new ArrayList<>();
							ResultSet rs = ps.executeQuery();
							while (rs.next()) {
//								RSATableSample rsaTableSample = new RSATableSample();
//								rsaTableSample.setId(rs.getString(tableId));
//								rsaTableSample.setSystem_name(rs.getString(sysNameStr));
//								rsaTableSample.setPrivate_key(rs.getString(privateKeyValue));
//								rsaTableSample.setPublic_key(rs.getString(publicKeyValue));
//								list.add(rsaTableSample);
								Map map = new HashMap<>();
								map.put("id", rs.getString(tableId));
								map.put("system_name", rs.getString(sysNameStr));
								map.put("private_key", rs.getString(privateKeyValue));
								map.put("public_key", rs.getString(publicKeyValue));
								list.add(map);
							}
							return list;
						}

					});
			map.put("data", list);
			map.put("type", Type.RSA.name().toLowerCase());
		} else if (properties.getType() == Type.CUSTOM) {
			throw new TokenCheckException("Sorry, do not support CUSTOM Type in webregister");
		} else {
			throw new TokenCheckException("Unsupported Type");
		}

		return map;

	}

	@DeleteMapping("/tokenData/{id}")
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
		} else if (properties.getType() == Type.CUSTOM) {
			throw new TokenCheckException("Sorry, do not support CUSTOM Type in webregister");
		} else {
			throw new TokenCheckException("Unsupported Type");
		}
		jdbcTemplate.update("delete from " + tableName + " where " + idName + " = ? ", id);
		return "success";
	}

	@DeleteMapping("/tokenData/all")
	public String deleteAll() throws Exception {
		String tableName = "tb_sys_token";
		if (properties.getType() == Type.SIMPLE) {
			tableName = AnnotationUtil.getAnnotationValue(SimpleTableSample.class, TableDesc.class);
		} else if (properties.getType() == Type.RSA) {
			tableName = AnnotationUtil.getAnnotationValue(RSATableSample.class, TableDesc.class);
		} else if (properties.getType() == Type.CUSTOM) {
			throw new TokenCheckException("Sorry, do not support CUSTOM Type in webregister");
		} else {
			throw new TokenCheckException("Unsupported Type");
		}
		jdbcTemplate.update("delete from " + tableName);
		return "success";
	}
}