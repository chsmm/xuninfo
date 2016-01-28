package com.xuninfo.zh.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Component
@ConfigurationProperties(prefix="crawler",locations="config/crawler.yml")
public class CrawlerConfig {
	
	private List<String> urls;

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}
	
	
	
	
}
