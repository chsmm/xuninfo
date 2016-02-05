package com.xuninfo.zh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Component
@ConfigurationProperties(prefix="crawler",locations="config/crawler.yml")
public class CrawlerConfig {
	
	private String url;
	
	private String limit;
	
	
	private Integer downloadThreadCount;
	
	private Integer pageProcessorThreadCount;

	private HttpConfig httpConfig;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}
	
	
	
	public Integer getDownloadThreadCount() {
		return downloadThreadCount;
	}

	public void setDownloadThreadCount(Integer downloadThreadCount) {
		this.downloadThreadCount = downloadThreadCount;
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
