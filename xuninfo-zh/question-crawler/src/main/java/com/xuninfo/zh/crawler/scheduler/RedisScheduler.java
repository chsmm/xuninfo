package com.xuninfo.zh.crawler.scheduler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.Scheduler;

import com.xuninfo.zh.store.RedisStore;
@Component
public class RedisScheduler implements Scheduler {

	@Autowired
	private RedisStore redisStore;
	
	public void push(Request request, Task task) {
		redisStore.addRequest(request.getUrl());
	}

	public Request poll(Task task) {
		String req = redisStore.getRequest();
		return !StringUtils.isEmpty(req)?new Request(req):null;
	}

}
