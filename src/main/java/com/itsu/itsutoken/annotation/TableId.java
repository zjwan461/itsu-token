package com.itsu.itsutoken.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.itsu.itsutoken.domain.IdType;

/**
 * @ClassName: TableId.java
 * @Description: token表的id主键字段
 * @author Jerry Su
 * @Date 2020年12月17日 下午4:35:26
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableId {

	String value() default "id";

	IdType type() default IdType.FAST_SIMPLE_UUID;

}