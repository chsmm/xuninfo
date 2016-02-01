package com.xuninfo.proxyCrawler.scheduled;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import com.xuninfo.proxyCrawler.config.SchedulerConfig;
import com.xuninfo.proxyCrawler.config.SchedulerDetail;
@Component
@Configuration
public class QuartzScheduledTasks {
	
	
	private MethodInvokingJobDetailFactoryBean createMethodInvokingJobDetailFactoryBean(SchedulerDetail schedulerDetail){
		MethodInvokingJobDetailFactoryBean bean = new MethodInvokingJobDetailFactoryBean();
		bean.setTargetBeanName(schedulerDetail.getSchedulerBeanName());
		bean.setTargetMethod(schedulerDetail.getSchedulerMethod());
		bean.setConcurrent(false);
		return bean;
	}
	
	@Bean(name="crawlerJobDetail")
	public MethodInvokingJobDetailFactoryBean crawlerJobDetailFactoryBean(SchedulerConfig schedulerConfig) {
		return createMethodInvokingJobDetailFactoryBean(schedulerConfig.getCrawler());
	}
	
	@Bean(name="verifyJobDetail")
	public MethodInvokingJobDetailFactoryBean verifyJobDetailFactoryBean(SchedulerConfig schedulerConfig) {
		return createMethodInvokingJobDetailFactoryBean(schedulerConfig.getVerify());
	}
	
	private Trigger createCronTrigger(JobDetail jobDetail,String cronExpression){
		return TriggerBuilder
		.newTrigger()
		.forJob(jobDetail)
		.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
		.build();
	}
	
	@Bean(name="crawlerCronTrigger")
	public Trigger crawlerCronTrigger(@Qualifier("crawlerJobDetail")MethodInvokingJobDetailFactoryBean crawlerJobDetail,SchedulerConfig schedulerConfig) {
		return createCronTrigger(crawlerJobDetail.getObject(),schedulerConfig.getCrawler().getCron());
	}
	
	@Bean(name="verifyCronTrigger")
	public Trigger verifyCronTriggerFactoryBean(@Qualifier("verifyJobDetail")MethodInvokingJobDetailFactoryBean verifyJobDetail,SchedulerConfig schedulerConfig) {
		return createCronTrigger(verifyJobDetail.getObject(),schedulerConfig.getVerify().getCron());
	}
	
	
	@Bean
    public SchedulerFactoryBean schedulerFactoryBean(MethodInvokingJobDetailFactoryBean[] jobDetailFactoryBeans,Trigger[] triggers) {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        JobDetail[] jobDetails = new JobDetail[jobDetailFactoryBeans.length];
        for (int i = 0; i < jobDetailFactoryBeans.length; i++) {
        	jobDetails[i] = jobDetailFactoryBeans[i].getObject();
		}
        scheduler.setJobDetails(jobDetails);
        scheduler.setTriggers(triggers);
        return scheduler;
    }
}
