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

import com.xuninfo.proxyCrawler.crawler.ProxyCrawler;
import com.xuninfo.proxyCrawler.crawler.http.support.HttpAsyncClientSupport;
import com.xuninfo.proxyCrawler.store.GuavaStore;

@Component("asyncDownload")
public class HttpAsyncClientDownload extends AbstractDownloader implements Runnable,IHandler {
	
	protected Logger logger= LoggerFactory.getLogger(getClass());
	
	@Autowired
	HttpAsyncClientSupport asyncClientSupport;
	
	@Autowired
	protected ProxyCrawler crawler;
	
	
	@Autowired
	GuavaStore<String, String> guavaStore;
	
	
	public HttpAsyncClientDownload() {
		
	}

	public Page download(final Request request, final Task task) {
		crawler.getExecutorService().execute(this);
		return null;
	}
	
	@Deprecated
	public void setThread(int threadNum) {

	}

	public void failed(String url,Exception exception) {
	/*	try{
			String error = exception.getMessage();
			if(StringUtils.isEmpty(error))redisStore.addFailedRequest(Joiner.on(':').join(DateTime.now().toString("yyyy-MM-dd hh:mm:ss"),url));
			else redisStore.addFailedRequest(Joiner.on(':').join(DateTime.now().toString("yyyy-MM-dd hh:mm:ss"),url,error));
		}catch(Exception e){
			logger.error("处理失败url异常", exception);
		}*/
		
	}


	public void completed(String url,HttpResponse httpResponse, String respBody) {		
		final Page page = new Page();
		page.setRawText(respBody);
		page.setRequest(new Request(url));
		crawler.getPageProcessor().process(page);
	}

	public void cancelled(String url) {
	/*	try{
			redisStore.addFailedRequest(Joiner.on(':').join(DateTime.now().toString("yyyy-MM-dd hh:mm:ss"),url));
		}catch(Exception e){
			logger.error("处理取消url异常", e);
		}*/
		
	}
	
	

	public void run() {
		List<String> urls = crawler.getUrls();
		for (String url : urls) {
			if(!StringUtils.isEmpty(url)){
				asyncClientSupport.doGet(url, this);
			}
		}
		
	}




	
	
	

}
