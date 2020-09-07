package com.itsu.itsutoken.configuration;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.itsu.itsutoken.checker.TokenChecker;
import com.itsu.itsutoken.table.TableSample;
import com.itsu.itsutoken.util.ServletUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.hutool.core.collection.CollectionUtil;

@Configuration
// @EnableConfigurationProperties(ItsuTokenProperties.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnClass({ TokenChecker.class })
@ConditionalOnProperty(name = "itsu-token.enable", havingValue = "true", matchIfMissing = true)
public class ItsuTokenAutoConfiguration {

    @Autowired
    private ItsuTokenProperties properties;

    @Bean
    public TokenChecker<? extends TableSample> tokenChecker(DataSource dataSource,
            DataSourceProperties dataSourceProperties) {
        TokenChecker<? extends TableSample> tokenChecker = properties.getType().generateTokenChecher();

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
        System.err.println(properties.getWebRegister().getRegisterUrl());
        return new WebMvcConfigurer() {

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new HandlerInterceptor() {

                    @Override
                    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                            throws Exception {
                        String registerUrl = properties.getWebRegister().getRegisterUrl();
                        String tokenListUrl = properties.getWebRegister().getTokenListUrl();
                        String requestURI = request.getRequestURI();
                        if (requestURI.endsWith(registerUrl) || requestURI.endsWith(tokenListUrl)) {
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

                        } else {
                            return true;
                        }

                    }
                }).addPathPatterns(properties.getWebRegister().getRegisterUrl(),
                        properties.getWebRegister().getTokenListUrl());
            }

        };
    }

}