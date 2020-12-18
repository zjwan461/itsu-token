package com.itsu.itsutoken.table;

import com.itsu.itsutoken.annotation.SimpleToken;
import com.itsu.itsutoken.annotation.SysName;
import com.itsu.itsutoken.annotation.TableDesc;
import com.itsu.itsutoken.annotation.TableId;

/**
 * 
 * @ClassName: SimpleTableSample.java
 * @Description: table sample for simple token type
 * @author Jerry Su
 * @Date 2020年12月17日 上午10:37:28
 */
@TableDesc
public class SimpleTableSample implements TableSample {

	@TableId
	private String id;

	@SimpleToken
	private String token;

	@SysName
	private String system_name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSystem_name() {
		return system_name;
	}

	public void setSystem_name(String system_name) {
		this.system_name = system_name;
	}

}