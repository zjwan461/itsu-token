package com.itsu.itsutoken.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @ClassName: ServletUtil.java
 * @Description: Http util for itsu-token
 * @author Jerry Su
 * @Date 2020年12月17日 上午11:29:38
 */
public class ServletUtil {

	private ServletUtil() {
	}

	private static ServletRequestAttributes getRequestAttributes() {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		return (ServletRequestAttributes) attributes;
	}

	/**
	 * 获取request
	 */
	public static HttpServletRequest getRequest() {
		return getRequestAttributes().getRequest();
	}

	/**
	 * 获取response
	 */
	public static HttpServletResponse getResponse() {
		return getRequestAttributes().getResponse();
	}

	/**
	 * 获取session
	 */
	public static HttpSession getSession() {
		return getRequest().getSession();
	}

	/**
	 * 获取String参数
	 */
	public static String getParameter(String name) {
		return getRequest().getParameter(name);
	}

	/**
	 * 获取token
	 *
	 * @return token信息
	 */
	public static String getToken() {
		return getHeader("token");

	}

	public static String getSystem() {
		return getHeader("system");
	}

	public static String getHeader(String key) {
		return getRequest().getHeader(key);
	}

	/**
	 * 是否是Ajax异步请求
	 *
	 * @return 结果
	 */
	public static boolean isAjaxRequest() {

		HttpServletRequest request = getRequest();
		String accept = request.getHeader("accept");
		if (accept != null && accept.indexOf("application/json") != -1) {
			return true;
		}

		String xRequestedWith = request.getHeader("X-Requested-With");
		if (xRequestedWith != null && xRequestedWith.indexOf("XMLHttpRequest") != -1) {
			return true;
		}

		return false;
	}

	/**
	 * 将字符串渲染到客户端
	 *
	 * @param string 待渲染的字符串
	 */
	public static void renderString(String string) {
		try {
			HttpServletResponse response = getResponse();
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(string);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断是否有token
	 *
	 * @return 结果
	 */
	public static boolean hasToken() {
		String token = (String) getRequest().getAttribute("token");
		return token != null && !token.trim().equals("");
	}

	/**
	 * 判断是否登录过系统
	 * 
	 * @return
	 */
	public static boolean isLogin() {
		String isLogin = (String) getSession().getAttribute("login");
		return "yes".equalsIgnoreCase(isLogin);
	}

	/**
	 * 
	 * @Title: login
	 * @Description: itsu-token webregister 登录方法
	 * @param:
	 * @return: void
	 * @throws @author suben
	 * @Date 2020年12月17日 上午10:36:01
	 */
	public static void login() {
		getSession().setAttribute("login", "yes");
	}
}