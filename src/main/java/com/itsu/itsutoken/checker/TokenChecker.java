package com.itsu.itsutoken.checker;

import com.itsu.itsutoken.exception.TokenCheckException;

import com.itsu.itsutoken.table.TableSample;
import org.aspectj.lang.JoinPoint;

public abstract class TokenChecker<T extends TableSample> {

    private T tableSample;

    public abstract void check(JoinPoint joinPoint) throws TokenCheckException;

    public T getTableSample() {
        return tableSample;
    }

    public void setTableSample(T tableSample) {
        this.tableSample = tableSample;
    }
}