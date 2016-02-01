package com.xuninfo.proxyCrawler.crawler.http;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.xuninfo.proxyCrawler.crawler.http.support.HttpAsyncClientSupport;
import com.xuninfo.proxyCrawler.crawler.impl.ProxyCrawler;
import com.xuninfo.proxyCrawler.store.RedisStore;

@Component("asyncDownload")
public class HttpAsyncClientDownload extends AbstractDownloader implements Runnable,IHandler {
	
	protected Logger logger= LoggerFactory.getLogger(getClass());
	
	@Autowired
	HttpAsyncClientSupport asyncClientSupport;
	
	@Autowired
	protected ProxyCrawler crawler;
	
	@Autowired
	private RedisStore redisStore;
	
	
	public HttpAsyncClientDownload() {
		
	}

	public Page download(final Request request, final Task task) {
		crawler.getExecutorService().execute(this);
		return null;
	}
	
	@Deprecated
	public void setThread(int threadNum) {

	}

	public void failed(String url,Exception e) {
		redisStore.addFailedRequest(Joiner.on(':').join(url,e.getMessage()));
	}


	public void completed(String url,HttpResponse httpResponse, String respBody) {
		final Page page = new Page();
		page.setRawText(respBody);
		page.setRequest(new Request(url));
		crawler.getPageProcessor().process(page);
	}

	public void cancelled(String url) {
		redisStore.addFailedRequest(url);
	}
	
	

	public void run() {
		List<String> urls = crawler.getUrls();
		for (String url : urls) {
			if(!StringUtils.isEmpty(url)){
				asyncClientSupport.doGet(url, crawler.getSite().getHeaders(), this);
			}
		}
		
	}




	
	
	

}
