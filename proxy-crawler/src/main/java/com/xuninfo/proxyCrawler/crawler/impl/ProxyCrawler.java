package com.xuninfo.proxyCrawler.crawler.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xuninfo.proxyCrawler.config.CrawlerConfig;
import com.xuninfo.proxyCrawler.crawler.AbstractCrawler;

@Component
public class ProxyCrawler extends AbstractCrawler {

	@Autowired
	private CrawlerConfig crawlerConfig;
	
	public List<String> getUrls(){
		return crawlerConfig.getUrls();
	}
	
	@Override
	protected void doInternalStart() {
		downloader.download(null, this);
	}

	
	
	
	
	
	

}
