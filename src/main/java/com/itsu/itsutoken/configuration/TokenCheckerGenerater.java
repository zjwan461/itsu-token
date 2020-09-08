package com.itsu.itsutoken.configuration;

import com.itsu.itsutoken.checker.TokenChecker;
import com.itsu.itsutoken.table.TableSample;

@FunctionalInterface
public interface TokenCheckerGenerater {
    TokenChecker<? extends TableSample> generateTokenChecker();
}