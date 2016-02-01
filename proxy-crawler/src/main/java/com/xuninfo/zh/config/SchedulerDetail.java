package com.xuninfo.zh.config;


public class SchedulerDetail {
	
	private String cron;
	private String schedulerBeanName;
	private String schedulerMethod;
	
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
	public String getSchedulerBeanName() {
		return schedulerBeanName;
	}
	public void setSchedulerBeanName(String schedulerBeanName) {
		this.schedulerBeanName = schedulerBeanName;
	}
	public String getSchedulerMethod() {
		return schedulerMethod;
	}
	public void setSchedulerMethod(String schedulerMethod) {
		this.schedulerMethod = schedulerMethod;
	}
}
