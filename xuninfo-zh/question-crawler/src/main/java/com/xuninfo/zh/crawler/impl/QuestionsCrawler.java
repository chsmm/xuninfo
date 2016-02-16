package com.xuninfo.zh.crawler.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xuninfo.zh.config.CrawlerConfig;
import com.xuninfo.zh.crawler.AbstractCrawler;

@Component
public class QuestionsCrawler extends AbstractCrawler {
	
	public volatile boolean isDone = false;	  
	
	@Override
	protected void doInternalStart() {
		downloader.download(null, this);
		pageProcessor.process(null);
	}

	
	
	
	
	
	

}
