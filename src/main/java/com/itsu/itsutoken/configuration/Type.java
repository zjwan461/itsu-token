package com.itsu.itsutoken.configuration;

import com.itsu.itsutoken.checker.RSATokenChecker;
import com.itsu.itsutoken.checker.SimpleTokenChecker;
import com.itsu.itsutoken.checker.TokenChecker;
import com.itsu.itsutoken.table.RSATableSample;
import com.itsu.itsutoken.table.SimpleTableSample;
import com.itsu.itsutoken.table.TableSample;

public enum Type implements TokenCheckerGenerater {
    SIMPLE {
        @Override
        public TokenChecker<? extends TableSample> generateTokenChecker() {
            return new SimpleTokenChecker(new SimpleTableSample());
        }
    },
    RSA {
        @Override
        public TokenChecker<? extends TableSample> generateTokenChecker() {
            return new RSATokenChecker(new RSATableSample());
        }
    },
    CUSTOM {
        @Override
        public TokenChecker<? extends TableSample> generateTokenChecker() {
            return null;
        }

    };

}