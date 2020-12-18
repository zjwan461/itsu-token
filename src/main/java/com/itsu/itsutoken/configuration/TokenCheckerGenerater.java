package com.itsu.itsutoken.configuration;

import com.itsu.itsutoken.checker.TokenChecker;
import com.itsu.itsutoken.table.TableSample;

/**
 * @ClassName: TokenCheckerGenerater.java
 * @Description: TODO token校验构建器
 * @author Jerry Su
 * @Date 2020年12月17日 下午4:17:29
 */
@FunctionalInterface
public interface TokenCheckerGenerater {

	/**
	 * @author Jerry Su
	 * @return
	 * @Description: 构建token checker校验器
	 * @Date 2020年12月17日 下午4:18:07
	 */
	TokenChecker<? extends TableSample> generateTokenChecker();
}