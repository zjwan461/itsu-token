package com.itsu.itsutoken.checker;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itsu.itsutoken.annotation.Token;
import com.itsu.itsutoken.exception.TokenCheckException;
import com.itsu.itsutoken.table.TableSample;

/**
 * @ClassName: TokenChecker.java
 * @Description: 用于token check的一个基类,定义了tablesample属性,指定了拦截规则
 * @author Jerry Su
 * @Date 2020年12月17日 下午4:25:08
 */
public abstract class TokenChecker<T extends TableSample> implements Checker {

	private static final Logger log = LoggerFactory.getLogger(TokenChecker.class);

	protected T tableSample;

	public T getTableSample() {
		return tableSample;
	}

	public void setTableSample(T tableSample) {
		this.tableSample = tableSample;
	}

	/**
	 * @author Jerry Su
	 * @Description: itsu token请求拦截规则
	 * @Date 2020年12月17日 下午4:33:46
	 */
	@Pointcut("@annotation(com.itsu.itsutoken.annotation.Token)")
	public void rule() {
	}

	/**
	 * @author Jerry Su
	 * @param joinPoint
	 * @param tokenAnno
	 * @throws TokenCheckException
	 * @Description: 判断是否需要拦截,需要则执行check方法
	 * @Date 2020年12月17日 下午4:34:06
	 */
	@Before("rule()&&@annotation(tokenAnno)")
	public void before(JoinPoint joinPoint, Token tokenAnno) throws TokenCheckException {
		if (tokenAnno.requried()) {
			check(joinPoint);
		} else {
			log.info("token marked required is false, jump token check");
		}
	}
}