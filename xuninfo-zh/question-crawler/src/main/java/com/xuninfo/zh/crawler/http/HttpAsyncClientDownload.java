package com.xuninfo.zh.crawler.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.selector.PlainText;

import com.google.common.base.Joiner;
import com.xuninfo.zh.config.CrawlerConfig;
import com.xuninfo.zh.crawler.http.support.HttpAsyncClientSupport;
import com.xuninfo.zh.crawler.impl.QuestionsCrawler;
import com.xuninfo.zh.crawler.scheduler.RedisScheduler;
import com.xuninfo.zh.store.PageStore;
import com.xuninfo.zh.store.RedisStore;

@Component("asyncDownload")
public class HttpAsyncClientDownload extends AbstractDownloader implements Runnable,IHandler {
	
	protected Logger logger= LoggerFactory.getLogger(getClass());
	
	@Autowired
	HttpAsyncClientSupport asyncClientSupport;
	
	@Autowired
	protected QuestionsCrawler crawler;
	
	@Autowired
	private CrawlerConfig crawlerConfig;
	
	@Autowired
	private RedisStore redisStore;
	
	@Autowired 
	private PageStore pageStore;
	
	public HttpAsyncClientDownload() {
		
	}

	public Page download(final Request request, final Task task) {
		int count = crawlerConfig.getDownloadThreadCount();
		for (int i = 0; i < count; i++) {
			crawler.getExecutorService().execute(this);	
		}
		return null;
	}
	
	@Deprecated
	public void setThread(int threadNum) {

	}

	public void failed(String url,Exception exception) {
		try{
			pageStore.addPageUrl(url);
			String error = exception.getMessage();
			if(StringUtils.isEmpty(error))redisStore.addFailedRequest(Joiner.on(':').join(DateTime.now().toString("yyyy-MM-dd hh:mm:ss"),url));
			else redisStore.addFailedRequest(Joiner.on(':').join(DateTime.now().toString("yyyy-MM-dd hh:mm:ss"),url,error));
		}catch(Exception e){
			logger.warn(url + "--处理失败url异常:"+exception.getMessage());
		}
		
	}



	public void completed(String url,HttpResponse httpResponse, String respBody) {
		Page page = new Page();
        page.setRawText(respBody);
        page.setUrl(new PlainText(url));
        page.setRequest(new Request(url));
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        pageStore.addPage(page);
		
	}

	public void cancelled(String url) {
		try{
			pageStore.addPageUrl(url);
			redisStore.addFailedRequest(Joiner.on(':').join(DateTime.now().toString("yyyy-MM-dd hh:mm:ss"),url));
		}catch(Exception e){
			logger.warn(url + "--处理取消url异常:"+e.getMessage());
		}
	}

	public void run() {
		RedisScheduler scheduler = crawler.getScheduler();
		while(!crawler.isDone||!scheduler.urlIsEmpty()){
			String url = scheduler.poll();
			if(!StringUtils.isEmpty(url)){
				asyncClientSupport.doGet(url, this);
			}
		}
		
	}



	
	
	

}
