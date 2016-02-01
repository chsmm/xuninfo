package com.xuninfo.proxyCrawler.crawler.processor;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import com.google.common.collect.Maps;
import com.xuninfo.proxyCrawler.config.ProcessorConfig;
import com.xuninfo.proxyCrawler.crawler.http.proxyVerify.ProxyVerify;
import com.xuninfo.proxyCrawler.crawler.impl.ProxyCrawler;
import com.xuninfo.proxyCrawler.store.RedisStore;
@Component
public class QuestionPageProcessor implements  PageProcessor,ApplicationContextAware {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Site site = Site.me()
	         .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
	         .setCharset("UTF-8")
	         .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
	         .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
			 .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	
	@Autowired 
	ProxyCrawler crawler;
	
	@Autowired
	RedisStore redisStore;
	
	@Autowired
	ProxyVerify proxyVerify;
	
	private Map<String, ProcessorStrategy> processorStrategys = Maps.newConcurrentMap();;
	
	public void process(Page page) {
		crawler.getExecutorService().execute(new ProcessorHandler(page));
	}

	public Site getSite() {
		return site;
	}
	

	
	class ProcessorHandler implements Runnable{
		private Page page;
		
		public ProcessorHandler(Page page) {
			this.page = page;
		}
		
		public void run() {
			Html html = Html.create(page.getRawText());
			String url = page.getRequest().getUrl();
			if(processorStrategys.containsKey(url)){
				List<String> hosts = processorStrategys.get(url).processor(html,url);
				if(!hosts.isEmpty()){
					proxyVerify.addVerifyQueue(hosts);
					//redisStore.addHttpProxy(hosts.toArray(new String[0]));
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setApplicationContext(ApplicationContext applicationContext)throws BeansException {
		ProcessorConfig processorConfig = applicationContext.getBean(ProcessorConfig.class);
		processorStrategys = Maps.newConcurrentMap();
		for(Map.Entry<String, String> entry:processorConfig.getProcessorStrategys().entrySet()){
			try {
				Class clazz = ClassUtils.getClass(entry.getValue());
				processorStrategys.put(entry.getKey(), (ProcessorStrategy)applicationContext.getBean(clazz));
			} catch (ClassNotFoundException e) {
				logger.error("初始化 processorStrategys 异常",e);
			}
			
		}
	}

}
