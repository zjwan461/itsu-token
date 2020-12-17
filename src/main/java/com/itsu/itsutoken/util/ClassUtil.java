package com.itsu.itsutoken.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.itsu.itsutoken.annotation.PrivateKey;
import com.itsu.itsutoken.annotation.PublicKey;
import com.itsu.itsutoken.annotation.SimpleToken;
import com.itsu.itsutoken.annotation.SysName;
import com.itsu.itsutoken.annotation.TableField;
import com.itsu.itsutoken.annotation.TableId;
import com.itsu.itsutoken.domain.IdType;
import com.itsu.itsutoken.table.TableSample;

import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import cn.hutool.core.annotation.AnnotationUtil;

/**
 * @ClassName: ClassUtil.java
 * @Description: class util for itsu-token annotation
 * @author Jerry Su
 * @Date 2020年12月17日 上午11:29:27
 */
public class ClassUtil {

	public static String getId(@NonNull Class<? extends TableSample> clazz) throws Exception {
		return getValue(clazz, TableId.class);
	}

	public static IdType getIdStrategy(@NonNull Class<? extends TableSample> clazz) throws Exception {
		IdType idType = null;
		List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
		for (Field field : fields) {
			if (field.isAnnotationPresent(TableId.class)) {
				idType = AnnotationUtil.getAnnotationValue(field, TableId.class, "type");
				break;
			}
		}
		return idType;
	}

	public static String getSysValue(@NonNull Class<? extends TableSample> clazz) throws Exception {
		return getValue(clazz, SysName.class);
	}

	public static String getSimpleTokenValue(@NonNull Class<? extends TableSample> clazz) throws Exception {
		return getValue(clazz, SimpleToken.class);
	}

	public static String getPrivateKeyValue(@NonNull Class<? extends TableSample> clazz) throws Exception {
		return getValue(clazz, PrivateKey.class);
	}

	public static String getPublicKeyValue(@NonNull Class<? extends TableSample> clazz) throws Exception {
		return getValue(clazz, PublicKey.class);
	}

	public static String getTableFieldValue(@NonNull Class<? extends TableSample> clazz) throws Exception {
		return getValue(clazz, TableField.class);
	}

	public static List<String> getTableFieldValues(@NonNull Class<? extends TableSample> clazz) throws Exception {
		return getValues(clazz, TableField.class);
	}

	private static String getValue(Class<? extends TableSample> clazz, Class<? extends Annotation> annotationType) {
		List<String> values = getValues(clazz, annotationType);
		if (values.size() > 0)
			return values.get(0);
		else
			return "";
	}

	private static List<String> getValues(Class<? extends TableSample> clazz,
			Class<? extends Annotation> annotationType) {
		List<String> list = new ArrayList<>();
		List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
		for (Field field : fields) {
			if (!StringUtils.isEmpty(AnnotationUtil.getAnnotationValue(field, annotationType))) {
				String value = AnnotationUtil.getAnnotationValue(field, annotationType);
				list.add(value);
			}
		}
		return list;
	}
}