package com.itsu.itsutoken.checker;

import org.aspectj.lang.JoinPoint;

import com.itsu.itsutoken.exception.TokenCheckException;

/**
 * @ClassName: Checker.java
 * @Description: token check类,实现它进行token校验
 * @author Jerry Su
 * @Date 2020年12月17日 下午4:23:56
 */
public interface Checker {
	/**
	 * @author Jerry Su
	 * @param joinPoint
	 * @throws TokenCheckException
	 * @Description: 在这个方法中进行接口校验
	 * @Date 2020年12月17日 下午4:24:30
	 */
	void check(JoinPoint joinPoint) throws TokenCheckException;
}
