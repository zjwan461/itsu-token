package com.itsu.itsutoken.checker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.util.StringUtils;

import com.itsu.itsutoken.annotation.TableDesc;
import com.itsu.itsutoken.configuration.ItsuTokenProperties;
import com.itsu.itsutoken.exception.TokenCheckException;
import com.itsu.itsutoken.table.SimpleTableSample;
import com.itsu.itsutoken.table.TableSample;
import com.itsu.itsutoken.util.ClassUtil;
import com.itsu.itsutoken.util.ServletUtil;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.extra.spring.SpringUtil;

/**
 * @ClassName: SimpleTokenChecker.java
 * @Description: 简单token校验类
 * @author Jerry Su
 * @Date 2020年12月17日 下午4:27:41
 */
@Aspect
public class SimpleTokenChecker extends TokenChecker<SimpleTableSample> {
	private static final Logger log = LoggerFactory.getLogger(SimpleTokenChecker.class);

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Resource
	private ItsuTokenProperties properties;

	public SimpleTokenChecker() {
		this.setTableSample(new SimpleTableSample());
	}

	@Override
	public void check(JoinPoint joinPoint) throws TokenCheckException {
		TableSample tableSample = null;
		try {
			tableSample = SpringUtil.getBean(TableSample.class);
			if (log.isDebugEnabled()) {
				log.debug("user set custom tableSample [" + tableSample.getClass().getName() + "]");
			}

			try {
				Integer value = (Integer) tableSample.getClass().getMethod("tip").invoke(tableSample);
				if (value.intValue() != 0) {
					throw new TokenCheckException(
							"if you set custom-schema to true you need provide a tableSample Class which implements com.itsu.itsutoken.table.TableSample and inject into Spring application context");
				}
			} catch (Exception e) {
				throw new TokenCheckException(e);
			}
		} catch (NoSuchBeanDefinitionException e) {
			tableSample = this.getTableSample();
			if (log.isDebugEnabled()) {
				log.debug("user do not set set custom tableSample, will use default ["
						+ tableSample.getClass().getName() + "]");
			}
		}

		String tableName = AnnotationUtil.getAnnotationValue(tableSample.getClass(), TableDesc.class);
		String simpleToken = null;
		try {
			simpleToken = ClassUtil.getSimpleTokenValue(tableSample.getClass());
			if (StringUtils.isEmpty(simpleToken)) {
				simpleToken = "token";
			}
		} catch (Exception e) {
			throw new TokenCheckException("get simple token field value fail", e);
		}

		String sysName = null;
		try {
			sysName = ClassUtil.getSysValue(tableSample.getClass());
			if (StringUtils.isEmpty(sysName)) {
				sysName = "sys_name";
			}
		} catch (Exception e) {
			throw new TokenCheckException("get system name field value fail", e);
		}

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		signature.getMethod();
		String system = ServletUtil.getSystem();
		if (!StringUtils.hasText(system)) {
			throw new TokenCheckException("request system is null or empty");
		}
		if (properties.getSystem().isEncryptBase64()) {
			system = Base64.decodeStr(system);
		}

		String token = ServletUtil.getToken();
		if (!StringUtils.hasText(token)) {
			throw new TokenCheckException("request token is null or empty");
		}

		final String systemStr = system;
		final String tokenStr = token;
		Integer count = jdbcTemplate.execute("select count(1) count from " + tableName + " where " + sysName
				+ " = ? and " + simpleToken + " = ? limit 1", new PreparedStatementCallback<Integer>() {

					@Override
					public Integer doInPreparedStatement(PreparedStatement ps)
							throws SQLException, DataAccessException {
						ps.setString(1, systemStr);
						ps.setString(2, tokenStr);
						ResultSet rs = ps.executeQuery();
						rs.next();
						return rs.getInt("count");
					}

				});
		if (count > 0) {
			if (log.isDebugEnabled())
				log.debug("token check pass, current request system is {}", systemStr);
		} else {
			throw new TokenCheckException("not valid system or token");
		}

	}

//	@Before("rule()&&@annotation(tokenAnno)")
//	public void before(JoinPoint joinPoint, Token tokenAnno) throws TokenCheckException {
//		if (tokenAnno.requried()) {
//			check(joinPoint);
//		} else {
//			log.info("token marked required is false, jump token check");
//		}
//	}

}