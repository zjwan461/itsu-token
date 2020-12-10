package com.itsu.itsutoken.checker;

import org.aspectj.lang.annotation.Pointcut;

import com.itsu.itsutoken.table.TableSample;

public abstract class TokenChecker<T extends TableSample> implements Checker {

	protected T tableSample;

	public T getTableSample() {
		return tableSample;
	}

	public void setTableSample(T tableSample) {
		this.tableSample = tableSample;
	}

	@Pointcut("@annotation(com.itsu.itsutoken.annotation.Token)")
	public void rule() {
	}
}