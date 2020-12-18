package com.itsu.itsutoken.checker;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.jdbc.core.JdbcTemplate;

import com.itsu.itsutoken.annotation.TableDesc;
import com.itsu.itsutoken.exception.TokenCheckException;
import com.itsu.itsutoken.table.TableSample;
import com.itsu.itsutoken.util.ClassUtil;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.extra.spring.SpringUtil;

/**
 * @ClassName: CusTokenChecker.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author Jerry Su
 * @Date 2020年12月17日 下午4:26:46
 */
public abstract class CusTokenChecker<T extends TableSample> implements Checker {

	protected T tableSample;

	public abstract T getTableSample();

	public void setTableSample(T tableSample) {
		this.tableSample = tableSample;
	}

	protected JdbcTemplate getJdbcTemplate() {
		return SpringUtil.getBean(JdbcTemplate.class);
	}

	@PostConstruct
	protected void generateTableSample() throws TokenCheckException {
		if (this.tableSample == null)
			this.tableSample = getTableSample();
		if (this.tableSample == null)
			throw new TokenCheckException("you must provide a tableSample for Custom Token check");
	}

	protected List<String> getTableFieldNames() throws TokenCheckException {
		try {
			return ClassUtil.getTableFieldValues(tableSample.getClass());
		} catch (Exception e) {
			throw new TokenCheckException(e);
		}
	}

	protected String getTableName() throws TokenCheckException {
		try {
			return AnnotationUtil.getAnnotationValue(tableSample.getClass(), TableDesc.class);
		} catch (UtilException e) {
			throw new TokenCheckException(e);
		}
	}

	protected String getTableId() throws TokenCheckException {
		try {
			return ClassUtil.getId(tableSample.getClass());
		} catch (Exception e) {
			throw new TokenCheckException(e);
		}
	}
}
