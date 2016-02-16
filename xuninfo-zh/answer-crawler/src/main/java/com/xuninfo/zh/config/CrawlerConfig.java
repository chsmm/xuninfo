package com.xuninfo.zh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Component
@ConfigurationProperties(prefix="crawler",locations="config/crawler.yml")
public class CrawlerConfig {
	
	private String url;
	
	private Integer page;
	
	private Long limitTime;
	
	
	private Integer downloadThreadCount;
	
	private Long downloadSleep;
	
	private Integer pageProcessorThreadCount;

	private HttpConfig httpConfig;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	

	public Long getLimitTime() {
		return limitTime;
	}

	public void setLimitTime(Long limitTime) {
		this.limitTime = limitTime;
	}

	public Integer getDownloadThreadCount() {
		return downloadThreadCount;
	}

	public void setDownloadThreadCount(Integer downloadThreadCount) {
		this.downloadThreadCount = downloadThreadCount;
	}
	

	public Long getDownloadSleep() {
		return downloadSleep;
	}

	public void setDownloadSleep(Long downloadSleep) {
		this.downloadSleep = downloadSleep;
	}

	public Integer getPageProcessorThreadCount() {
		return pageProcessorThreadCount;
	}

	public void setPageProcessorThreadCount(Integer pageProcessorThreadCount) {
		this.pageProcessorThreadCount = pageProcessorThreadCount;
	}

	public HttpConfig getHttpConfig() {
		return httpConfig;
	}

	public void setHttpConfig(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;
	}
	
}
