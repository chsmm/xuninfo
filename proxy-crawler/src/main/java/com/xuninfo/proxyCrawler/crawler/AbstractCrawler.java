package com.xuninfo.proxyCrawler.crawler;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.processor.PageProcessor;

import com.google.common.util.concurrent.AbstractService;
import com.xuninfo.proxyCrawler.crawler.support.ExecutorServiceUtil;

public abstract class AbstractCrawler extends AbstractService implements Crawler,Task {
	
	protected Logger logger =LoggerFactory.getLogger(getClass());
	
	@Autowired
	protected Downloader downloader;
	
	@Autowired
	protected PageProcessor pageProcessor;
	
	
	protected Site site;
	
	protected ExecutorService executorService;
	
	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public ExecutorService getExecutorService() {
		return this.executorService==null ? ExecutorServiceUtil.getExecutorService() : executorService;
	}
	
	

	public String getUUID() {
		return UUID.randomUUID().toString();
	}

	public Site getSite() {
		return pageProcessor.getSite();
	}
	
	
	public Downloader getDownloader() {
		return downloader;
	}

	public void setDownloader(Downloader downloader) {
		this.downloader = downloader;
	}

	public PageProcessor getPageProcessor() {
		return pageProcessor;
	}

	public void setPageProcessor(PageProcessor pageProcessor) {
		this.pageProcessor = pageProcessor;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	@Override
	protected void doStart() {
		logger.info("Start Proxy Crawler  At Time:"+DateTime.now().toString("yyyy-MM-dd hh:mm:ss"));
		doInternalStart();
	}
	
	protected abstract void doInternalStart();
	

	@Override
	protected void doStop() {
		logger.info("End Proxy Crawler At Time:"+DateTime.now().toString("yyyy-MM-dd hh:mm:ss"));
		executorService.shutdown();
	}

	

}
