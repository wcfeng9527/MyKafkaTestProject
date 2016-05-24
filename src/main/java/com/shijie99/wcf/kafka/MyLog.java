package com.shijie99.wcf.kafka;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class MyLog implements Serializable{
	private static final long serialVersionUID = -514541550600383768L;
	private int id;
	private String message;
	private String name;
	
	public MyLog(int id, String message, String name) {
		super();
		this.id = id;
		this.message = message;
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		//直接转换成json字符串
		return JSON.toJSONString(this);
	}
}
