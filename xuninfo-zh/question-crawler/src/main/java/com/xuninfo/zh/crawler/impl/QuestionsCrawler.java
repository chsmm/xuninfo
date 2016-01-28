package com.xuninfo.zh.crawler.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Request;

import com.xuninfo.zh.config.CrawlerConfig;
import com.xuninfo.zh.crawler.AbstractCrawler;

@Component
public class QuestionsCrawler extends AbstractCrawler {

	@Autowired
	private CrawlerConfig crawlerConfig;
	
	public volatile boolean isDone = false;
	
	public Long getLimitDate(){
		return DateTime.parse(crawlerConfig.getLimit()).getMillis();
	}
	  
	
	@Override
	protected void doInternalStart() {
		Request request = new Request(crawlerConfig.getUrl());
		scheduler.push(request, this);
		downloader.download(null, null);
		pageProcessor.process(null);
	}

	
	
	
	
	
	

}
