package com.xuninfo.zh.crawler.processor;

import java.util.List;
import java.util.Map;

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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xuninfo.zh.config.CrawlerConfig;
import com.xuninfo.zh.crawler.impl.AnswerCrawler;
import com.xuninfo.zh.store.PageStore;
import com.xuninfo.zh.store.RedisStore;
@Component
public class AnswerPageProcessor implements Runnable, PageProcessor {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired 
	AnswerCrawler crawler;
	
	@Autowired
	RedisStore redisStore;
	
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
			String url = page.getUrl().get();
			Html html = page.getHtml();
			List<Selectable> questionItem = html.xpath("//div[@class=\"question-item\"]").nodes();
			if(null==questionItem||questionItem.isEmpty()){
				pageStore.addPageUrl(url);
				continue;
			};
			try{
				List<String> answerQuestions = Lists.newArrayList();
				List<String> notAnswerQuestions = Lists.newArrayList();
				Map<String, String> questionInfo =null;
				for(Selectable question : questionItem){
					String data_timestamp = question.xpath("div/h2/span/@data-timestamp").get();
					if(limitDate>=Long.parseLong(data_timestamp)){
						if(!crawler.isDone){
							crawler.isDone=true;
						}
					}
					questionInfo = Maps.newHashMap();
					questionInfo.put("question_link", question.xpath("div/h2/a/@href").get());
					questionInfo.put("question", question.xpath("div/h2/a/text()").get());
					questionInfo.put("time", data_timestamp);
					int content = Integer.parseInt(question.xpath("meta/@content").get());
					String questionInfoJson = JSON.toJSONString(questionInfo);
					if( content==0) notAnswerQuestions.add(questionInfoJson);
					else answerQuestions.add(questionInfoJson);
				}
				if(!answerQuestions.isEmpty())redisStore.addAnswerQuestion(answerQuestions.toArray(new String[0]));
				if(!notAnswerQuestions.isEmpty())redisStore.addNotAnswerQuestion(notAnswerQuestions.toArray(new String[0]));
			}catch(Exception e){
				pageStore.addPageUrl(url);
			}
		}
		
	}

}
