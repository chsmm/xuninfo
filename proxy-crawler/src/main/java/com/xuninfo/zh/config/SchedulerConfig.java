package com.xuninfo.zh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="scheduler",locations="config/scheduler.yml")
public class SchedulerConfig {
	
	private SchedulerDetail crawler;
	private SchedulerDetail verify;
	
	public SchedulerDetail getCrawler() {
		return crawler;
	}

	public void setCrawler(SchedulerDetail crawler) {
		this.crawler = crawler;
	}
	public SchedulerDetail getVerify() {
		return verify;
	}

	public void setVerify(SchedulerDetail verify) {
		this.verify = verify;
	}

	
}
