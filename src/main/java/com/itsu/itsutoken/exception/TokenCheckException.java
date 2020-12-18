package com.itsu.itsutoken.exception;

/**
 * 
 * @ClassName: TokenCheckException.java
 * @Description: 校验token异常
 * @author Jerry Su
 * @Date 2020年12月17日 上午10:39:04
 */
public class TokenCheckException extends Exception {

	private static final long serialVersionUID = 1858323711681488180L;

	public TokenCheckException(String message) {
		super(message);
	}

	public TokenCheckException(Throwable t) {
		super(t);
	}

	public TokenCheckException(String message, Throwable t) {
		super(message, t);
	}

}