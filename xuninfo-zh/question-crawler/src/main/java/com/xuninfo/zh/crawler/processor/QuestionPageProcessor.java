package com.xuninfo.zh.crawler.processor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.xuninfo.zh.crawler.impl.QuestionsCrawler;
import com.xuninfo.zh.store.PageStore;
import com.xuninfo.zh.store.RedisStore;
@Component
public class QuestionPageProcessor implements Runnable, PageProcessor {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired 
	QuestionsCrawler crawler;
	
	@Autowired
	RedisStore redisStore;
	
	@Autowired 
	private PageStore pageStore;
	
	@Autowired
	private CrawlerConfig crawlerConfig;
	
	
	public void process(Page page) {
		int count = crawlerConfig.getPageProcessorThreadCount();
		for (int i = 0; i < count; i++) {
			crawler.getExecutorService().execute(this);
		}
		
	}

	public Site getSite() {
		return null;
	}

	public void run() {
		long limitDate = crawler.getLimitDate();
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
				createNextRequest(url);
				List<String> answerQuestions = Lists.newArrayList();
				List<String> notAnswerQuestions = Lists.newArrayList();
				Map<String, String> questionInfo =null;
				boolean su=false;
				for(Selectable question : questionItem){
					String timestamp = question.xpath("div/h2/span/@data-timestamp").get();
					if(limitDate-Long.parseLong(timestamp)>0){
						crawler.isDone=true;
						break;
					}
					questionInfo = Maps.newHashMap();
					questionInfo.put("question_link", question.xpath("div/h2/a/@href").get());
					questionInfo.put("question", question.xpath("div/h2/a/text()").get());
					questionInfo.put("time", timestamp);
					int content = Integer.parseInt(question.xpath("meta/@content").get());
					String questionInfoJson = JSON.toJSONString(questionInfo);
					su = content==0 ? notAnswerQuestions.add(questionInfoJson):answerQuestions.add(questionInfoJson);
				}
				if(!answerQuestions.isEmpty())redisStore.addAnswerQuestion(answerQuestions.toArray(new String[0]));
				if(!notAnswerQuestions.isEmpty())redisStore.addNotAnswerQuestion(notAnswerQuestions.toArray(new String[0]));
			}catch(Exception e){
				pageStore.addPageUrl(url);
			}
		}
		
	}

	
	/**
	 * 
	 * @param html
	 */
	private void createNextRequest(String curPage){
		int index = curPage.indexOf("=")+1;
		String url = curPage.substring(0,index);
		url +=  (Long.parseLong(curPage.substring(index))+1);
		pageStore.addPageUrl(url);
	}
	

}
