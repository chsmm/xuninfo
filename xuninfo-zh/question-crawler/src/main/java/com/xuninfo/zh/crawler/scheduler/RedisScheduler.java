package com.xuninfo.zh.crawler.scheduler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.Scheduler;

import com.xuninfo.zh.store.PageStore;
@Component
public class RedisScheduler implements Scheduler {

	@Autowired
	private PageStore pageStore;
	
	public void push(Request request, Task task) {
		push(request.getUrl());
	}

	public Request poll(Task task) {
		String req = pageStore.getPageUrl();
		return !StringUtils.isEmpty(req)?new Request(req):null;
	}
	
	public void push(String url) {
		pageStore.addPageUrl(url);
	}

	public String poll() {
		String req = pageStore.getPageUrl();
		return !StringUtils.isEmpty(req)?req:null;
	}
	
	public boolean urlIsEmpty(){
		return pageStore.urlIsEmpty();
	}

}
