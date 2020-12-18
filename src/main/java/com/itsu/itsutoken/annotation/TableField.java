package com.itsu.itsutoken.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: TableField.java
 * @Description: 用于自定义校验的token表字段名称
 * @author Jerry Su
 * @Date 2020年12月17日 下午4:35:18
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableField {
	String value();

}
