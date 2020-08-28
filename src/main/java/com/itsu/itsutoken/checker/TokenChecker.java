package com.itsu.itsutoken.checker;

import com.itsu.itsutoken.exception.TokenCheckException;

import org.aspectj.lang.JoinPoint;

@FunctionalInterface
public interface TokenChecker {
    
    void check(JoinPoint joinPoint) throws TokenCheckException;
}