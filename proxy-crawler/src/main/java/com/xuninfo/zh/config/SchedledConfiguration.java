package com.xuninfo.zh.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
@Component
@Configuration
public class SchedledConfiguration {

	// 配置中设定了
	// ① targetMethod: 指定需要定时执行scheduleInfoAction中的simpleJobTest()方法
	// ② concurrent：对于相同的JobDetail，当指定多个Trigger时, 很可能第一个job完成之前，
	// 第二个job就开始了。指定concurrent设为false，多个job不会并发运行，第二个job将不会在第一个job完成之前开始。
	// ③ cronExpression：0/10 * * * * ?表示每10秒执行一次，具体可参考附表。
	// ④ triggers：通过再添加其他的ref元素可在list中放置多个触发器。
	// scheduleInfoAction中的simpleJobTest()方法
	@Bean
	public MethodInvokingJobDetailFactoryBean detailFactoryBean(SchedulerConfig schedulerConfig) {
		MethodInvokingJobDetailFactoryBean bean = new MethodInvokingJobDetailFactoryBean();
		bean.setTargetBeanName(schedulerConfig.getSchedulerClassName());
		bean.setTargetMethod(schedulerConfig.getSchedulerMethod());
		bean.setConcurrent(false);
		return bean;
	}

	@Bean
	public Trigger cronTriggerBean(
			MethodInvokingJobDetailFactoryBean detailFactoryBean,SchedulerConfig schedulerConfig) {
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.forJob(detailFactoryBean.getObject())
				.withSchedule(CronScheduleBuilder.cronSchedule(schedulerConfig.getCron()))
				.build();
		return trigger;
	}

	@Bean
	public SchedulerFactoryBean schedulerFactory(Trigger[] cronTriggerBean,MethodInvokingJobDetailFactoryBean detailFactoryBean) {
		SchedulerFactoryBean bean = new SchedulerFactoryBean();
		bean.setJobDetails(detailFactoryBean.getObject());
		bean.setTriggers(cronTriggerBean);
		return bean;
	}
}
