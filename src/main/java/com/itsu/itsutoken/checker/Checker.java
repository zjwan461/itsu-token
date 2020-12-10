package com.itsu.itsutoken.checker;

import org.aspectj.lang.JoinPoint;

import com.itsu.itsutoken.exception.TokenCheckException;

public interface Checker {
	void check(JoinPoint joinPoint) throws TokenCheckException;
}
