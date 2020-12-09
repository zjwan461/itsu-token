package com.itsu.itsutoken.configuration;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
	public TokenChecker<? extends TableSample> tokenChecker(ItsuTokenProperties properties)
			throws TokenConfigureException {

		TokenCheckerGenerater tokenCheckerGenerater = SpringUtil.getBean(TokenCheckerGenerater.class);
		if (tokenCheckerGenerater == null) {
			throw new TokenConfigureException(
					"You must create a bean implements TokenCheckerGenerater and aware into IOC because of the token type is CUSTOM");
		}
		TokenChecker<? extends TableSample> tokenChecker = tokenCheckerGenerater.generateTokenChecker();
		return tokenChecker;
	}

	@Bean
	@ConditionalOnProperty(name = "type", prefix = "itsu-token", havingValue = "SIMPLE", matchIfMissing = true)
	public SimpleTokenChecker simpleTokenChecker() {
		return new SimpleTokenChecker();
	}

	@Bean
	@ConditionalOnProperty(name = "type", prefix = "itsu-token", havingValue = "RSA", matchIfMissing = false)
	public RSATokenChecker RSATokenChecker() {
		return new RSATokenChecker();
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