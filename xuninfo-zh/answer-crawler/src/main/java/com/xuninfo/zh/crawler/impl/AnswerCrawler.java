package com.xuninfo.zh.crawler.impl;

import org.springframework.stereotype.Component;

import com.xuninfo.zh.crawler.AbstractCrawler;

@Component
public class AnswerCrawler extends AbstractCrawler {
	
	public volatile boolean isDone = false;	  
	
	@Override
	protected void doInternalStart() {
		downloader.download(null, this);
		pageProcessor.process(null);
	}

	
	
	
	
	
	

}
