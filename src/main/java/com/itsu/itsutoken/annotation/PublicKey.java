package com.itsu.itsutoken.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: PublicKey.java
 * @Description: RSA校验类型的public key字段
 * @author Jerry Su
 * @Date 2020年12月17日 下午4:34:56
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PublicKey {
	String value() default "public_key";
}