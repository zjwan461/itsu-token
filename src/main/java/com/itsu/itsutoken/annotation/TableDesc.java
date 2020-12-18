package com.itsu.itsutoken.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: TableDesc.java
 * @Description: token表名
 * @author Jerry Su
 * @Date 2020年12月17日 下午4:35:13
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableDesc {

	String value() default "tb_sys_token";

}