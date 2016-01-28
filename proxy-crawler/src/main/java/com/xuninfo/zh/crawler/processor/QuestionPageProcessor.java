package com.xuninfo.zh.crawler.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import com.xuninfo.zh.crawler.impl.ProxyCrawler;
import com.xuninfo.zh.store.RedisStore;
@Component
public class QuestionPageProcessor implements Runnable, PageProcessor {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Site site = Site.me().setRetryTimes(3).setSleepTime(1500).setTimeOut(3 * 60 * 1000)
	         .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
	         .setCharset("UTF-8")
	         .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
	         .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
			 .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	
	@Autowired 
	ProxyCrawler crawler;
	
	@Autowired
	RedisStore redisStore;
	
	public void process(Page page) {
		crawler.getExecutorService().execute(this);
	}

	public Site getSite() {
		return site;
	}

	public void run() {
		
		
	}

	

	

}
