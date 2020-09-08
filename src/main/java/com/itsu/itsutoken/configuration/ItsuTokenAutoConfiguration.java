package com.itsu.itsutoken.configuration;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.itsu.itsutoken.checker.TokenChecker;
import com.itsu.itsutoken.exception.TokenConfigureException;
import com.itsu.itsutoken.table.TableSample;
import com.itsu.itsutoken.util.ServletUtil;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnClass({ TokenChecker.class, DataSourceAutoConfiguration.class })
@ConditionalOnProperty(name = "itsu-token.enable", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class ItsuTokenAutoConfiguration {

    @Resource
    private ItsuTokenProperties properties;

    @Bean
    public TokenChecker<? extends TableSample> tokenChecker(DataSource dataSource,
            DataSourceProperties dataSourceProperties) throws TokenConfigureException {
        TokenChecker<? extends TableSample> tokenChecker = null;
        Type type = properties.getType();
        if (type == Type.CUSTOM) {
            TokenCheckerGenerater tokenCheckerGenerater = SpringUtil.getBean(TokenCheckerGenerater.class);
            if (tokenCheckerGenerater == null) {
                throw new TokenConfigureException(
                        "You must create a bean implements TokenCheckerGenerater and aware into IOC because of the token type is CUSTOM");
            }
            tokenChecker = tokenCheckerGenerater.generateTokenChecker();
        } else {
            tokenChecker = type.generateTokenChecker();
        }

        if (properties.getInit().isAutoCreateTable()) {
            if (CollectionUtil.isEmpty(dataSourceProperties.getSchema())) {
                String schemaLocation = properties.getInit().getSchemaLocation();
                dataSourceProperties.setSchema(Arrays.asList(schemaLocation));
            }
            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer(dataSource, dataSourceProperties);
            dataSourceInitializer.createSchema();
        }

        return tokenChecker;
    }

    @Bean
    public WebMvcConfigurer tokenRegisterWebMvcConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                String registerUrl = properties.getWebRegister().getRegisterUrl();
                String tokenListUrl = properties.getWebRegister().getTokenListUrl();
                if (!StrUtil.startWith(registerUrl, "/")) {
                    registerUrl += "/" + registerUrl;
                }
                if (!StrUtil.startWith(tokenListUrl, "/")) {
                    tokenListUrl += "/" + tokenListUrl;
                }
                final String registerUrlStr = registerUrl;
                final String tokenListUrlStr = tokenListUrl;
                registry.addInterceptor(new HandlerInterceptor() {

                    @Override
                    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                            throws Exception {

                        if (properties.getWebRegister().isEnable()) {
                            if (ServletUtil.isLogin()) {
                                return true;
                            } else {
                                response.getWriter().write("Authorization error");
                                response.setStatus(401);
                                response.sendRedirect("login.html");
                                return false;
                            }
                        } else {
                            response.getWriter().write("itsu-token.web-register is not set to true");
                            return false;
                        }

                    }
                }).addPathPatterns(registerUrlStr, tokenListUrlStr).addPathPatterns(Constants.LOGIN_IN_URLS);
            }

        };
    }

    public static class Constants {
        public static final List<String> LOGIN_IN_URLS = Arrays.asList("/tokenListUrl", "/tokenRegisterUrl",
                "/tokenData/**", "/tokenRegister/**");
    }
}