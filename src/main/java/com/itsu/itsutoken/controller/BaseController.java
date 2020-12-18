package com.itsu.itsutoken.controller;

import java.io.IOException;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itsu.itsutoken.configuration.ItsuTokenProperties;
import com.itsu.itsutoken.util.ServletUtil;

import cn.hutool.core.io.IoUtil;

/**
 * @ClassName: BaseController.java
 * @Description: webregister 的基本功能controller
 * @author Jerry Su
 * @Date 2020年12月17日 上午10:49:12
 */
public class BaseController extends SuperController {

	@javax.annotation.Resource
	private ItsuTokenProperties properties;

	/**
	 * @author Jerry Su
	 * @return
	 * @throws IOException
	 * @Description: 访问register token 页面
	 * @Date 2020年12月17日 上午11:02:35
	 */
	@RequestMapping("#{itsuTokenProperties.webRegister.registerUrl}")
	public String registerPage() throws IOException {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource("classpath:html/registerToken.html");
		return IoUtil.read(resource.getInputStream(), "UTF-8");
	}

	/**
	 * 
	 * @author Jerry Su
	 * @param user
	 * @param password
	 * @return String
	 * @Description: webregister 登录接口
	 * @Date 2020年12月17日 上午11:00:54
	 */
	@PostMapping("/tokenLogin")
	public String login(String user, String password) {
		if (properties.getWebRegister().getUser().equals(user)
				&& properties.getWebRegister().getPassword().equals(password)) {
			ServletUtil.login();
			return properties.getWebRegister().getRegisterUrl();
		} else {
			ServletUtil.getResponse().setStatus(401);
			return "Authorization error";
		}
	}

	/**
	 * @author Jerry Su
	 * @return
	 * @throws Exception
	 * @Description: 访问webregister登录页面
	 * @Date 2020年12月17日 上午11:55:51
	 */
	@GetMapping("#{itsuTokenProperties.webRegister.loginUrl}")
	public String toLogin() throws Exception {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource("classpath:html/login.html");
		return IoUtil.read(resource.getInputStream(), "UTF-8");
	}

	/**
	 * @author Jerry Su
	 * @return
	 * @Description: token list列表页面
	 * @Date 2020年12月17日 上午11:56:33
	 */
	@GetMapping("/tokenListUrl")
	public String listUrl() {
		return properties.getWebRegister().getTokenListUrl();
	}

	/**
	 * @author Jerry Su
	 * @return
	 * @Description: token 注册页面
	 * @Date 2020年12月17日 上午11:56:48
	 */
	@GetMapping("/tokenRegisterUrl")
	public String listRegisterUrl() {
		return properties.getWebRegister().getRegisterUrl();
	}

	/**
	 * @author Jerry Su
	 * @return
	 * @throws Exception
	 * @Description: token list页面
	 * @Date 2020年12月17日 上午11:57:06
	 */
	@GetMapping("#{itsuTokenProperties.webRegister.tokenListUrl}")
	public String tokenListPage() throws Exception {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource("classpath:html/tokenList.html");
		return IoUtil.read(resource.getInputStream(), "UTF-8");
	}

}