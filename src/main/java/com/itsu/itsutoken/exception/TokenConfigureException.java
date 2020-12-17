package com.itsu.itsutoken.exception;

/**
 * 
 * @ClassName: TokenConfigureException.java
 * @Description: itsu-token 配置异常
 * @author Jerry Su
 * @Date 2020年12月17日 上午10:39:50
 */
public class TokenConfigureException extends Exception {

	private static final long serialVersionUID = 6527763755266003951L;

	public TokenConfigureException(String message) {
		super(message);
	}

	public TokenConfigureException(Throwable t) {
		super(t);
	}

	public TokenConfigureException(String message, Throwable t) {
		super(message, t);
	}
}