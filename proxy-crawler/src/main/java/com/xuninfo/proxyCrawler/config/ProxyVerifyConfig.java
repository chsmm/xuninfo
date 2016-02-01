package com.xuninfo.proxyCrawler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="verify",locations="config/proxyVerifyConfig.yml")
public class ProxyVerifyConfig {
	
	private String host;
	private String feature;
	private Integer verifyQueueSize;
	private Integer verifyQueueTimeout;
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getFeature() {
		return feature;
	}
	public void setFeature(String feature) {
		this.feature = feature;
	}
	public Integer getVerifyQueueSize() {
		return verifyQueueSize;
	}
	public void setVerifyQueueSize(Integer verifyQueueSize) {
		this.verifyQueueSize = verifyQueueSize;
	}
	public Integer getVerifyQueueTimeout() {
		return verifyQueueTimeout;
	}
	public void setVerifyQueueTimeout(Integer verifyQueueTimeout) {
		this.verifyQueueTimeout = verifyQueueTimeout;
	}
	
	
	

}
