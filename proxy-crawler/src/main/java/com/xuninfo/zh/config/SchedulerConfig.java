package com.xuninfo.zh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="scheduler",locations="config/scheduler.yml")
public class SchedulerConfig {
	private String cron;
	private String schedulerClassName;
	private String schedulerMethod;
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
	public String getSchedulerClassName() {
		return schedulerClassName;
	}
	public void setSchedulerClassName(String schedulerClassName) {
		this.schedulerClassName = schedulerClassName;
	}
	public String getSchedulerMethod() {
		return schedulerMethod;
	}
	public void setSchedulerMethod(String schedulerMethod) {
		this.schedulerMethod = schedulerMethod;
	}
	
}
