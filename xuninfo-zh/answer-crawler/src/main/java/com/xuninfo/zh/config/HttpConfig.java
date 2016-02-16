package com.xuninfo.zh.config;

import java.util.Map;

public class HttpConfig {
	private Integer connectTimeout;
	private Integer connectMaxTotal;
	private String encoding;
	private Boolean proxy;
	private Map<String, String> headers;
	
	public Integer getConnectTimeout() {
		return connectTimeout;
	}
	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	public Integer getConnectMaxTotal() {
		return connectMaxTotal;
	}
	public void setConnectMaxTotal(Integer connectMaxTotal) {
		this.connectMaxTotal = connectMaxTotal;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public Boolean getProxy() {
		return proxy;
	}
	public void setProxy(Boolean proxy) {
		this.proxy = proxy;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	
	
}
