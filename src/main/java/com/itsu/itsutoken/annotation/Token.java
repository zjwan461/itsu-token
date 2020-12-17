package com.itsu.itsutoken.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: Token.java
 * @Description: 校验token接口的切入点
 * @author Jerry Su
 * @Date 2020年12月17日 下午4:35:32
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Token {
	boolean requried() default true;
}