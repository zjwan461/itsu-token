package com.itsu.itsutoken.configuration;

import com.itsu.itsutoken.checker.TokenChecker;
import com.itsu.itsutoken.table.TableSample;

public interface TokenCheckerGenerater {
    TokenChecker<? extends TableSample> generateTokenChecher();
}