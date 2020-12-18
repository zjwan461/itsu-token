package com.itsu.itsutoken.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: SysName.java
 * @Description: 系统代号/名称字段
 * @author Jerry Su
 * @Date 2020年12月17日 下午4:35:06
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SysName {
	String value() default "sys_name";
}