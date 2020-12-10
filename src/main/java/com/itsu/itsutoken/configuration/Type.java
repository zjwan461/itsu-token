package com.itsu.itsutoken.configuration;

import com.itsu.itsutoken.checker.CustomTokenChecker;
import com.itsu.itsutoken.checker.RSATokenChecker;
import com.itsu.itsutoken.checker.SimpleTokenChecker;
import com.itsu.itsutoken.checker.TokenChecker;
import com.itsu.itsutoken.table.TableSample;

public enum Type implements TokenCheckerGenerater {
    SIMPLE {
        @Override
        public TokenChecker<? extends TableSample> generateTokenChecker() {
            return new SimpleTokenChecker();
        }
    },
    RSA {
        @Override
        public TokenChecker<? extends TableSample> generateTokenChecker() {
            return new RSATokenChecker();
        }
    },
    CUSTOM {
        @Override
        public TokenChecker<? extends TableSample> generateTokenChecker() {
            return new CustomTokenChecker();
        }

    };

}