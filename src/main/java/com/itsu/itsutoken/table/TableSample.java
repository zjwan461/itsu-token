package com.itsu.itsutoken.table;

/**
 * @ClassName: TableSample.java
 * @Description: table sample interface
 * @author Jerry Su
 * @Date 2020年12月17日 上午11:30:47
 */
public interface TableSample {

	/**
	 *  @author Jerry Su
	 *  @return
	 *  @Description: 判断是使用者提供的table sample还是,系统默认提供的 
	 *  @Date 2020年12月17日 上午11:31:37
	 */
	default Integer tip() {
		return 0;
	}
}