package com.itsu.itsutoken.checker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.itsu.itsutoken.annotation.TableDesc;
import com.itsu.itsutoken.annotation.Token;
import com.itsu.itsutoken.configuration.ItsuTokenProperties;
import com.itsu.itsutoken.exception.TokenCheckException;
import com.itsu.itsutoken.table.RSATableSample;
import com.itsu.itsutoken.table.TableSample;
import com.itsu.itsutoken.util.ClassUtil;
import com.itsu.itsutoken.util.IocUtil;
import com.itsu.itsutoken.util.ServletUtil;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

@Aspect
@Component
@ConditionalOnProperty(name = "type", prefix = "itsu-token", havingValue = "RSA", matchIfMissing = false)
public class RSATokenChecker extends TokenChecker<RSATableSample> {
	private static final Logger log = LoggerFactory.getLogger(RSATokenChecker.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ItsuTokenProperties properties;

	public RSATokenChecker() {
		this.setTableSample(new RSATableSample());
	}

	@Pointcut("@annotation(com.itsu.itsutoken.annotation.Token)")
	public void rule() {
	}

	@Before("rule()&&@annotation(tokenAnno)")
	public void before(JoinPoint joinPoint, Token tokenAnno) throws TokenCheckException {
		if (tokenAnno.requried()) {
			check(joinPoint);
		} else {
			log.info("token marked required is false, jump token check");
		}
	}

	@Override
	public void check(JoinPoint joinPoint) throws TokenCheckException {
		TableSample tableSample = IocUtil.getBean(TableSample.class);
		if (tableSample != null) {
			if (log.isDebugEnabled()) {
				log.debug("user set custom tableSample [" + tableSample.getClass().getName() + "]");
			}
			if (tableSample.getClass().isInterface()) {
				throw new TokenCheckException(
						"if you set custom-schema to true you need provide a tableSample Class which implements com.itsu.itsutoken.table.TableSample and inject into Spring application context");
			}
		} else {
			tableSample = this.getTableSample();
			if (log.isDebugEnabled()) {
				log.debug("user do not set set custom tableSample, will use default ["
						+ tableSample.getClass().getName() + "]");
			}
		}

		tableSample = this.getTableSample();
		String tableName = AnnotationUtil.getAnnotationValue(tableSample.getClass(), TableDesc.class);

		String privateKeyName = null;
		try {
			privateKeyName = ClassUtil.getPrivateKeyValue(tableSample.getClass());
		} catch (Exception e) {
			throw new TokenCheckException("get privateKey value fail", e);
		}

		String publicKeyName = null;
		try {
			publicKeyName = ClassUtil.getPublicKeyValue(tableSample.getClass());
		} catch (Exception e) {
			throw new TokenCheckException("get publicKey value fail", e);
		}
		String sysName = null;
		try {
			sysName = ClassUtil.getSysValue(tableSample.getClass());
		} catch (Exception e) {
			throw new TokenCheckException("get system name field value fail", e);
		}

		String system = ServletUtil.getSystem();
		if (!StringUtils.hasText(system)) {
			throw new TokenCheckException("request system is null or empty");
		}
		if (properties.getSystem().isEncryptBase64()) {
			system = Base64.decodeStr(system);
		}

		String token = ServletUtil.getToken();
		if (!StringUtils.hasText(system)) {
			throw new TokenCheckException("request token is null or empty");
		}

		final String systemStr = system;
		final String sysNameStr = sysName;
		final String privateKeyStr = privateKeyName;
		final String publicKeyStr = publicKeyName;
		List<RSATableSample> rsaList = jdbcTemplate.execute("select " + sysName + "," + privateKeyName + ","
				+ publicKeyName + " from " + tableName + " where " + sysName + " = ? ",
				new PreparedStatementCallback<List<RSATableSample>>() {

					@Override
					public List<RSATableSample> doInPreparedStatement(PreparedStatement ps)
							throws SQLException, DataAccessException {
						List<RSATableSample> list = new ArrayList<>();
						ps.setString(1, systemStr);
						ResultSet rs = ps.executeQuery();
						while (rs.next()) {
							RSATableSample rsaTs = new RSATableSample();
							rsaTs.setPrivate_key(rs.getString(privateKeyStr));
							rsaTs.setPublic_key(rs.getString(publicKeyStr));
							rsaTs.setSystem_name(rs.getString(sysNameStr));
							list.add(rsaTs);
						}
						return list;
					}
				});

		if (CollectionUtil.isEmpty(rsaList)) {
			throw new TokenCheckException("can not found any system " + system + " in current system");
		}

		boolean result = false;
		for (RSATableSample rs : rsaList) {
			RSA rsa = new RSA(rs.getPrivate_key(), null);
			try {
				String decryptStr = rsa.decryptStr(token, KeyType.PrivateKey);
				if (system.equals(decryptStr)) {
					result = true;
					break;
				}
			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.debug("decrypt Rsa token fail", e);
				}
			}
		}

		if (!result) {
			throw new TokenCheckException("check rsa token fail, Verification failed ");
		}

		if (log.isDebugEnabled()) {
			log.debug(" check rsa success, which request system is {}",
					properties.getSystem().isEncryptBase64() ? Base64.decodeStr(system) : system);
		}
	}

}