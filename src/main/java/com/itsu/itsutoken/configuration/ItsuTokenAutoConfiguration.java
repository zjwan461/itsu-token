package com.itsu.itsutoken.configuration;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.itsu.itsutoken.checker.CusTokenChecker;
import com.itsu.itsutoken.checker.CustomTokenChecker;
import com.itsu.itsutoken.checker.RSATokenChecker;
import com.itsu.itsutoken.checker.SimpleTokenChecker;
import com.itsu.itsutoken.checker.TokenChecker;
import com.itsu.itsutoken.controller.BaseController;
import com.itsu.itsutoken.controller.TokenListController;
import com.itsu.itsutoken.controller.TokenRegisterController;
import com.itsu.itsutoken.exception.TokenConfigureException;
import com.itsu.itsutoken.table.TableSample;
import com.itsu.itsutoken.table.TableSampleAdaptor;
import com.itsu.itsutoken.util.ServletUtil;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;

/**
 * @ClassName: ItsuTokenAutoConfiguration.java
 * @Description: itsu-token Spring Boot自动配置类
 * @author suben
 * @Date 2020年12月15日 上午11:45:30
 */
@Configuration
@ConditionalOnClass({ TokenChecker.class, DataSourceAutoConfiguration.class })
@ConditionalOnProperty(name = "itsu-token.enable", havingValue = "true", matchIfMissing = false)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ItsuTokenAutoConfiguration {

	@Bean
	public ItsuTokenProperties itsuTokenProperties() {
		return new ItsuTokenProperties();
	}

	@Bean
	@ConditionalOnProperty(name = "type", prefix = "itsu-token", havingValue = "CUSTOM", matchIfMissing = false)
	@ConditionalOnBean(CusTokenChecker.class)
	public CustomTokenChecker customTokenChecker(ItsuTokenProperties properties, CusTokenChecker checker)
			throws TokenConfigureException {

//		CusTokenChecker checker = null;
//		try {
//			checker = SpringUtil.getBean(CusTokenChecker.class);
//		} catch (NoSuchBeanDefinitionException e) {
//			throw new TokenConfigureException(
//					"You must create a bean implements TokenChecker and aware into IOC because of the token type is CUSTOM",
//					e);
//		}
		CustomTokenChecker customTokenChecker = (CustomTokenChecker) Type.CUSTOM.generateTokenChecker();
		customTokenChecker.setChecker(checker);
		return customTokenChecker;
	}

	@Bean
	@ConditionalOnProperty(name = "type", prefix = "itsu-token", havingValue = "SIMPLE", matchIfMissing = true)
	public SimpleTokenChecker simpleTokenChecker() {
		return (SimpleTokenChecker) Type.SIMPLE.generateTokenChecker();
	}

	@Bean
	@ConditionalOnProperty(name = "type", prefix = "itsu-token", havingValue = "RSA", matchIfMissing = false)
	public RSATokenChecker RSATokenChecker() {
		return (RSATokenChecker) Type.RSA.generateTokenChecker();
	}

	@Bean
	@ConditionalOnProperty(prefix = "itsu-token.init", name = "custom-schema", havingValue = "true", matchIfMissing = false)
	@ConditionalOnMissingBean(value = TableSample.class)
	public TableSample tableSample() {
		return new TableSampleAdaptor();
	}

	@Bean
	public DataSourceInitializer dataSourceInitializer(DataSource dataSource, DataSourceProperties dataSourceProperties,
			ItsuTokenProperties properties) {
		DataSourceInitializer dataSourceInitializer = new DataSourceInitializer(dataSource, dataSourceProperties);
		if (properties.getInit().isAutoCreateTable()) {
			// 如果spring jdbc的schema sql脚本为null，则执行itsu-token中设置的schema脚本
			if (CollectionUtil.isEmpty(dataSourceProperties.getSchema())) {
				String schemaLocation = properties.getInit().getSchemaLocation();
				dataSourceProperties.setSchema(Arrays.asList(schemaLocation));
			}
			dataSourceInitializer.createSchema();
		}
		return dataSourceInitializer;
	}

	@Bean
	@ConditionalOnProperty(name = "itsu-token.web-register.enable", havingValue = "true", matchIfMissing = false)
	public BaseController BaseController() {
		return new BaseController();
	}

	@Bean
	@ConditionalOnProperty(name = "itsu-token.web-register.enable", havingValue = "true", matchIfMissing = false)
	public TokenListController tokenListController() {
		return new TokenListController();
	}

	@Bean
	@ConditionalOnProperty(name = "itsu-token.web-register.enable", havingValue = "true", matchIfMissing = false)
	public TokenRegisterController tokenRegisterController() {
		return new TokenRegisterController();
	}

	@Bean
	public SpringUtil springUtil() {
		return new SpringUtil();
	}

	@Bean
	public WebMvcConfigurer tokenRegisterWebMvcConfigurer(ItsuTokenProperties properties) {
		return new WebMvcConfigurer() {

			private String reloadUrl(String url) {
				if (!StrUtil.startWith(url, "/")) {
					url = "/" + url;
				}
				return "/" + Constants.PROJECT_NAME + url;
			}

			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				String loginUrl = this.reloadUrl(properties.getWebRegister().getLoginUrl());
				String type = properties.getType().name();

				final String loginUrlStr = loginUrl;
				final String typeStr = type;
				registry.addInterceptor(new HandlerInterceptor() {

					@Override
					public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
							throws Exception {

						if (properties.getWebRegister().isEnable()) {
							if (Type.CUSTOM.name().equals(typeStr)) {
								response.getWriter().write("Custom type do not support Web-register");
								response.setStatus(HttpStatus.FORBIDDEN.value());
								return false;
							}

							if (ServletUtil.isLogin()) {
								return true;
							} else {
								response.getWriter().write("Authorization error");
								response.setStatus(HttpStatus.UNAUTHORIZED.value());
								response.sendRedirect(loginUrlStr);
								return false;
							}
						} else {
							response.getWriter().write("itsu-token.web-register is not set to true");
							response.setStatus(HttpStatus.FORBIDDEN.value());
							return false;
						}

					}
				}).addPathPatterns(Constants.INTERCEPTOR_PATTERN).excludePathPatterns(loginUrl,
						Constants.SUB_TO_LOGIN_URL);
			}

		};
	}

	public static class Constants {
		public static final String PROJECT_NAME = "itsu-token";

		public static final String INTERCEPTOR_PATTERN = "/" + PROJECT_NAME + "/**";

		public static final String SUB_TO_LOGIN_URL = "/" + PROJECT_NAME + "/tokenLogin";
	}

}