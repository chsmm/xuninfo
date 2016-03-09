package com.xuninfo.zh.crawler.processor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xuninfo.zh.config.CrawlerConfig;
import com.xuninfo.zh.crawler.impl.QuestionsCrawler;
import com.xuninfo.zh.store.PageStore;
import com.xuninfo.zh.store.ParameterStore;
import com.xuninfo.zh.store.RedisStore;
@Component
public class QuestionPageProcessor implements Runnable, PageProcessor {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired 
	QuestionsCrawler crawler;
	
	@Autowired
	RedisStore redisStore;
	
	
	@Autowired
	ParameterStore parameterStore;
	
	@Autowired 
	private PageStore pageStore;
	
	@Autowired
	private CrawlerConfig crawlerConfig;
	
	private Long limitDate;
	
	
	public void process(Page page) {
		limitDate = crawlerConfig.getLimitTime();
		if(null==limitDate){
			limitDate = DateTime.now().getMillis();
		}
		int count = crawlerConfig.getPageProcessorThreadCount();
		for (int i = 0; i < count; i++) {
			crawler.getExecutorService().execute(this);
		}
		
	}

	public Site getSite() {
		return null;
	}

	public void run() {
		
		while(!crawler.isDone||!pageStore.pageIsEmpty()){
			
			Page page = pageStore.getPage();
			if(null==page)continue;
			try{
				List<Selectable> questionItem = null;
				@SuppressWarnings("unchecked")
				Map<String, Object> map  = JSON.parseObject(page.getRawText(), Map.class);
				JSONArray jsonArray = ((JSONArray)map.get("msg"));
				String questionStr =jsonArray.getString(1);
				int size = jsonArray.getInteger(0);
				if(size==0 && StringUtils.isEmpty((questionStr))){
					if(crawler.isDone)continue;
					logger.info("Done");
					crawler.isDone=true;
				}
				else if(StringUtils.isEmpty(questionStr)){
					logger.info("Empty questionStr url:"+page.getUrl().get());
					parameterStore.put(page.getUrl().get());
					continue;
				};
				questionItem = Html.create(questionStr).xpath("div[@class=\"feed-item\"").nodes();
				if(null==questionItem||questionItem.isEmpty()){
					logger.info("Empty questionItem url:"+page.getUrl().get());
					parameterStore.put(page.getUrl().get());
					continue;
				}
				parameterStore.put(questionItem.get(questionItem.size() - 1).xpath("div/@data-score").get());
				Set<String> urls =Sets.newLinkedHashSetWithExpectedSize(questionItem.size());
				String questionUrl = null;
				for (Selectable question : questionItem) {
					questionUrl= question.xpath("//h2").links().get();
					if(!questionUrl.startsWith("https://www.zhihu.com")){
						questionUrl = "https://www.zhihu.com"+questionUrl;
					}
					urls.add(questionUrl);
				}
				redisStore.addQuestions(urls.toArray(new String[0]));
				logger.info("process end");
			}catch(Exception e){
				logger.warn(page.getUrl().get());
				parameterStore.put(page.getUrl().get());
			}
		}
		
	}

}
