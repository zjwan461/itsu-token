package com.itsu.itsutoken.checker;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;

import com.itsu.itsutoken.exception.TokenCheckException;

@Aspect
public class CustomTokenChecker extends TokenChecker {
//	private static final Logger log = LoggerFactory.getLogger(TokenChecker.class);

	private CusTokenChecker checker;

	public CustomTokenChecker() {
	}

	@Override
	public void check(JoinPoint joinPoint) throws TokenCheckException {
		checker.check(joinPoint);
	}

	public CusTokenChecker getChecker() {
		return checker;
	}

	public void setChecker(CusTokenChecker checker) {
		this.checker = checker;
	}

//	@Before("rule()&&@annotation(tokenAnno)")
//	public void before(JoinPoint joinPoint, Token tokenAnno) throws TokenCheckException {
//		if (tokenAnno.requried()) {
//			check(joinPoint);
//		} else {
//			log.info("token marked required is false, jump token check");
//		}
//	}
}
