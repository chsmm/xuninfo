package com.xuninfo.zh.crawler.http;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.scheduler.Scheduler;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.xuninfo.zh.crawler.http.support.HttpAsyncClientSupport;
import com.xuninfo.zh.crawler.impl.ProxyCrawler;
import com.xuninfo.zh.store.RedisStore;

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

	public void failed(Request request,Exception e) {
		redisStore.addFailedRequest(request.getUrl()+":"+e.getMessage());
	}



	public void completed(Request request,HttpResponse httpResponse, String respBody) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("respBody", respBody);
		map.put("url", request.getUrl());
		map.put("request", request);
		map.put("statusCode", httpResponse.getStatusLine().getStatusCode());
		redisStore.addPage(JSON.toJSONString(map));
	}
	
	/**
	 * 创建page对象
	 * @param request
	 * @param httpResponse
	 * @param respBody
	 * @return
	 */
	/*private Page createPage(Request request,HttpResponse httpResponse, String respBody){
		
		Page page = new Page();
        page.setRawText(respBody);
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        return page;
	}*/



	public void cancelled(Request request) {
		redisStore.addFailedRequest(request.getUrl());
	}

	public void run() {
		List<String> urls = crawler.getUrls();
		for (String url : urls) {
			final Request request = new Request(url);
			if(null!=request){
				asyncClientSupport.doGet(request, crawler.getSite(), this);
			}
		}
		
	}



	
	
	

}
