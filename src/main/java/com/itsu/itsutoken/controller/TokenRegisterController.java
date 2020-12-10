package com.itsu.itsutoken.controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itsu.itsutoken.annotation.TableDesc;
import com.itsu.itsutoken.configuration.ItsuTokenProperties;
import com.itsu.itsutoken.configuration.Type;
import com.itsu.itsutoken.domain.IdType;
import com.itsu.itsutoken.exception.TokenCheckException;
import com.itsu.itsutoken.table.RSATableSample;
import com.itsu.itsutoken.table.SimpleTableSample;
import com.itsu.itsutoken.table.TableSample;
import com.itsu.itsutoken.util.ClassUtil;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.extra.spring.SpringUtil;

@RequestMapping("/tokenRegister")
public class TokenRegisterController extends SuperController {
	private static final Logger log = LoggerFactory.getLogger(TokenRegisterController.class);

	@javax.annotation.Resource
	private JdbcTemplate jdbcTemplate;

	@javax.annotation.Resource
	private ItsuTokenProperties properties;

	@GetMapping("/generate/{system}")
	public Map<String, Object> generateToken(@PathVariable("system") @NonNull String system) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String tableName = "tb_sys_token";
		String sysName = "sys_name";
		String id = "id";
		IdType idType = IdType.FAST_SIMPLE_UUID;
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
			id = ClassUtil.getId(tableSampleClass);
			idType = ClassUtil.getIdStrategy(tableSampleClass);
			if (this.checkSystem(sysName, system, tableName)) {
				String token = ClassUtil.getSimpleTokenValue(tableSampleClass);
				String generateToken = IdUtil.fastSimpleUUID();
				jdbcTemplate.update(
						"insert into " + tableName + " ( " + id + "," + sysName + "," + token + ") value (?,?,?)",
						idType.generateId(), system, generateToken);
				map.put("token", generateToken);
				map.put("status", true);
			} else {
				map.put("errorMsg", "Duplicate system name");
			}
		} else if (properties.getType() == Type.RSA) {
			if (tableSampleClass == null)
				tableSampleClass = RSATableSample.class;
			tableName = AnnotationUtil.getAnnotationValue(tableSampleClass, TableDesc.class);
			sysName = ClassUtil.getSysValue(tableSampleClass);
			id = ClassUtil.getId(tableSampleClass);
			idType = ClassUtil.getIdStrategy(tableSampleClass);
			if (this.checkSystem(sysName, system, tableName)) {
				String privateKey = ClassUtil.getPrivateKeyValue(tableSampleClass);
				String publicKey = ClassUtil.getPublicKeyValue(tableSampleClass);
				final RSA rsa = new RSA();
				String generatePrivateKey = rsa.getPrivateKeyBase64();
				String generatePublicKey = rsa.getPublicKeyBase64();
				jdbcTemplate.update(
						"insert into " + tableName + " ( " + id + "," + sysName + "," + privateKey + "," + publicKey
								+ ") value (?,?,?,?)",
						idType.generateId(), system, generatePrivateKey, generatePublicKey);
				map.put("publicKey", generatePublicKey);
				map.put("privateKey", generatePrivateKey);
				map.put("status", true);
			} else {
				map.put("errorMsg", "Duplicate system name");
			}
		} else if (properties.getType() == Type.CUSTOM) {
			throw new TokenCheckException("Sorry, do not support CUSTOM Type in webregister");
		} else {
			throw new TokenCheckException("Unsupported Type [ " + properties.getType().name() + " ]");
		}

		return map;
	}

	@GetMapping("/type")
	public Map<String, Object> getType() {
		Map<String, Object> map = new HashMap<>();
		map.put("status", true);
		map.put("type", properties.getType().name().toLowerCase());
		return map;
	}

	public boolean checkSystem(String sysName, String system, String tableName) {
		Integer count = jdbcTemplate.execute("select count(1) num from " + tableName + " where " + sysName + " = ?",
				new PreparedStatementCallback<Integer>() {

					@Override
					public Integer doInPreparedStatement(PreparedStatement ps)
							throws SQLException, DataAccessException {
						ps.setString(1, system);
						ResultSet rs = ps.executeQuery();
						rs.next();
						return rs.getInt("num");
					}

				});
		return count == 0;
	}
}