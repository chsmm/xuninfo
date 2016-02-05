package com.xuninfo.zh.config;

import java.util.Map;
import java.util.Set;

public class HttpConfig {
	private Integer connectTimeout;
	private Integer connectMaxTotal;
	private String encoding;
	private Boolean proxy;
	private Set<Integer> retryStatusCode;
	private Set<Integer> giveupStatusCode;
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
	public Set<Integer> getRetryStatusCode() {
		return retryStatusCode;
	}
	public void setRetryStatusCode(Set<Integer> retryStatusCode) {
		this.retryStatusCode = retryStatusCode;
	}
	public Set<Integer> getGiveupStatusCode() {
		return giveupStatusCode;
	}
	public void setGiveupStatusCode(Set<Integer> giveupStatusCode) {
		this.giveupStatusCode = giveupStatusCode;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	
	
}
