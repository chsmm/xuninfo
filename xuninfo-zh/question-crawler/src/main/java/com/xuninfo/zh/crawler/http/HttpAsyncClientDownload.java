package com.xuninfo.zh.crawler.http;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.selector.PlainText;

import com.xuninfo.zh.config.CrawlerConfig;
import com.xuninfo.zh.crawler.http.support.HttpAsyncClientSupport;
import com.xuninfo.zh.crawler.impl.QuestionsCrawler;
import com.xuninfo.zh.store.PageStore;
import com.xuninfo.zh.store.ParameterStore;
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
	
	@Autowired 
	ParameterStore parameterStore;
	
	private String url;
	
	private AtomicBoolean init = new AtomicBoolean(false);
	
	private Long downloadSleep;
	
	public HttpAsyncClientDownload() {
		
	}

	public Page download(final Request request, final Task task) {
		url = crawlerConfig.getUrl();
		downloadSleep = crawlerConfig.getDownloadSleep();
		int count = crawlerConfig.getDownloadThreadCount();
		for (int i = 0; i < count; i++) {
			crawler.getExecutorService().execute(this);
		}
		return null;
	}
	
	

	public void failed(Map<String, String> parameters,Exception exception) {
		try{
			parameterStore.put(parameters.get("offset"));
			//pageStore.addPageUrl(url);
			//String error = exception.getMessage();
			//if(StringUtils.isEmpty(error))redisStore.addFailedRequest(Joiner.on(':').join(DateTime.now().toString("yyyy-MM-dd hh:mm:ss"),url));
			//else redisStore.addFailedRequest(Joiner.on(':').join(DateTime.now().toString("yyyy-MM-dd hh:mm:ss"),url,error));
		}catch(Exception e){
			logger.warn(url + "--failed 异常:"+exception.getMessage());
		}
		
	}

	public void completed(Map<String, String> parameters,HttpResponse httpResponse, String respBody) {
		if(!init.get()){
			init.compareAndSet(false, true);
		}
		Page page = new Page();
        page.setRawText(respBody);
        page.setUrl(new PlainText(parameters==null ? null:parameters.get("offset")));
        page.setRequest(new Request(url));
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        pageStore.addPage(page);
		
	}

	public void cancelled(Map<String, String> parameters) {
		try{
			parameterStore.put(parameters.get("offset"));
			//pageStore.addPageUrl(url);
			//redisStore.addFailedRequest(Joiner.on(':').join(DateTime.now().toString("yyyy-MM-dd hh:mm:ss"),url));
		}catch(Exception e){
			logger.warn(url + "--cancelled 异常:"+e.getMessage());
		}
	}

	public void run() {
		asyncClientSupport.doPost(url, null, this);
		while(!crawler.isDone || !parameterStore.isEmpty()){
			Map<String, String> parameters = parameterStore.poll();
			if(init.get() && null==parameters)continue;
			asyncClientSupport.doPost(url,parameters, this);
			try {
				Thread.sleep(downloadSleep);
			} catch (InterruptedException e) {
				logger.warn("download异常:"+e.getMessage());
			}
		}	
	}
	
	@Deprecated
	public void setThread(int threadNum) {}

}
