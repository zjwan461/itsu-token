package com.itsu.itsutoken.checker;

import com.itsu.itsutoken.table.TableSample;

public abstract class CusTokenChecker<T extends TableSample> implements Checker {

	protected T tableSample;

	public abstract T getTableSample();

	public void setTableSample(T tableSample) {
		this.tableSample = tableSample;
	}

}
