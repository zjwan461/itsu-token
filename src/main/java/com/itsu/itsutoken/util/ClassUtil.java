package com.itsu.itsutoken.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import com.itsu.itsutoken.annotation.PrivateKey;
import com.itsu.itsutoken.annotation.PublicKey;
import com.itsu.itsutoken.annotation.SimpleToken;
import com.itsu.itsutoken.annotation.SysName;
import com.itsu.itsutoken.table.RSATableSample;
import com.itsu.itsutoken.table.SimpleTableSample;
import com.itsu.itsutoken.table.TableSample;

import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import cn.hutool.core.annotation.AnnotationUtil;

public class ClassUtil {

    public static String getSysValue(@NonNull Class<? extends TableSample> clazz) throws Exception {
        String sysValue = null;
        if (SimpleTableSample.class.isAssignableFrom(clazz)) {
            sysValue = getValue(clazz, SysName.class);
        } else if (RSATableSample.class.isAssignableFrom(clazz)) {
            sysValue = getValue(clazz, SysName.class);
        } else {
            throw new Exception("table sample must extends " + TableSample.class.getName());
        }
        return sysValue;
    }

    public static String getSimpleTokenValue(@NonNull Class<? extends TableSample> clazz) throws Exception {
        String tokenValue = null;
        if (SimpleTableSample.class.isAssignableFrom(clazz)) {
            tokenValue = getValue(clazz, SimpleToken.class);
        } else if (RSATableSample.class.isAssignableFrom(clazz)) {
            tokenValue = getValue(clazz, SimpleToken.class);
        } else {
            throw new Exception("table sample must extends " + TableSample.class.getName());
        }
        return tokenValue;
    }

    public static String getPrivateKeyValue(@NonNull Class<? extends TableSample> clazz) throws Exception {
        String privateKeyValue = null;
        if (SimpleTableSample.class.isAssignableFrom(clazz)) {
            privateKeyValue = getValue(clazz, PrivateKey.class);
        } else if (RSATableSample.class.isAssignableFrom(clazz)) {
            privateKeyValue = getValue(clazz, PrivateKey.class);
        } else {
            throw new Exception("table sample must extends " + TableSample.class.getName());
        }
        return privateKeyValue;
    }

    public static String getPublicKeyValue(@NonNull Class<? extends TableSample> clazz) throws Exception {
        String publicKeyValue = null;
        if (SimpleTableSample.class.isAssignableFrom(clazz)) {
            publicKeyValue = getValue(clazz, PublicKey.class);
        } else if (RSATableSample.class.isAssignableFrom(clazz)) {
            publicKeyValue = getValue(clazz, PublicKey.class);
        } else {
            throw new Exception("table sample must extends " + TableSample.class.getName());
        }
        return publicKeyValue;
    }

    private static String getValue(Class<? extends TableSample> clazz, Class<? extends Annotation> annotationType) {
        String value = null;
        List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
        for (Field field : fields) {
            if (!StringUtils.isEmpty(AnnotationUtil.getAnnotationValue(field, annotationType))) {
                value = AnnotationUtil.getAnnotationValue(field, annotationType);
            }
        }
        return value;
    }
}