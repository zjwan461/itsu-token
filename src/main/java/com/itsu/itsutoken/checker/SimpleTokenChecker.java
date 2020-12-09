package com.itsu.itsutoken.checker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
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
import com.itsu.itsutoken.table.SimpleTableSample;
import com.itsu.itsutoken.util.ClassUtil;
import com.itsu.itsutoken.util.ServletUtil;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.codec.Base64;

@Aspect
@Component
@ConditionalOnProperty(name = "type", prefix = "itsu-token", havingValue = "SIMPLE", matchIfMissing = true)
public class SimpleTokenChecker extends TokenChecker<SimpleTableSample> {
    private static final Logger log = LoggerFactory.getLogger(SimpleTokenChecker.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ItsuTokenProperties properties;

    @Pointcut("@annotation(com.itsu.itsutoken.annotation.Token)")
    public void rule() {
    }

    public SimpleTokenChecker() {
        this.setTableSample(new SimpleTableSample());
    }

    @Override
    public void check(JoinPoint joinPoint) throws TokenCheckException {
        SimpleTableSample tableSample = this.getTableSample();
        String tableName = AnnotationUtil.getAnnotationValue(tableSample.getClass(), TableDesc.class);
        if (StringUtils.isEmpty(tableName)) {
            tableName = "tb_sys_token";
        }
        String simpleToken = null;
        try {
            simpleToken = ClassUtil.getSimpleTokenValue(SimpleTableSample.class);
            if (StringUtils.isEmpty(simpleToken)) {
                simpleToken = "token";
            }
        } catch (Exception e) {
            throw new TokenCheckException("get simple token field value fail", e);
        }

        String sysName = null;
        try {
            sysName = ClassUtil.getSysValue(SimpleTableSample.class);
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

    @Before("rule()&&@annotation(tokenAnno)")
    public void before(JoinPoint joinPoint, Token tokenAnno) throws TokenCheckException {
        if (tokenAnno.requried()) {
            check(joinPoint);
        } else {
            log.info("token marked required is false, jump token check");
        }
    }

}