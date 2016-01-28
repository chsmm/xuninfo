package com.xuninfo.zh.crawler.processor;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xuninfo.zh.crawler.impl.QuestionsCrawler;
import com.xuninfo.zh.store.RedisStore;
@Component
public class QuestionPageProcessor implements Runnable, PageProcessor {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Site site = Site.me().setRetryTimes(3).setSleepTime(1500).setTimeOut(3 * 60 * 1000)
	         .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
	         .setCharset("UTF-8")
	         .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
	         .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
			 .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	
	@Autowired 
	QuestionsCrawler crawler;
	
	@Autowired
	RedisStore redisStore;
	
	public void process(Page page) {
		crawler.getExecutorService().execute(this);
	}

	public Site getSite() {
		return site;
	}

	public void run() {
		long limitDate = crawler.getLimitDate();
		while(true){
			String pageStr = redisStore.getPage();
			if(StringUtils.isEmpty(pageStr))continue;
			Page page = createPage(pageStr);
			Html html = page.getHtml();
			createNextRequest(page.getRequest().getUrl());
			List<Selectable> questionItem = html.xpath("//div[@class=\"question-item\"]").nodes();
			List<String> answerQuestions = Lists.newArrayList();
			List<String> notAnswerQuestions = Lists.newArrayList();
			Map<String, String> questionInfo =null;
			for(Selectable question : questionItem){
				String timestamp = question.xpath("div/h2/span/@data-timestamp").get();
				if(limitDate-Long.parseLong(timestamp)>0)break;
				questionInfo = Maps.newHashMap();
				questionInfo.put("question_link", question.xpath("div/h2/a/@href").get());
				questionInfo.put("question", question.xpath("div/h2/a/text()").get());
				questionInfo.put("time", timestamp);
				int content = Integer.parseInt(question.xpath("meta/@content").get());
				String questionInfoJson = JSON.toJSONString(questionInfo);
				boolean su = content==0 ? notAnswerQuestions.add(questionInfoJson):answerQuestions.add(questionInfoJson);
			}
			if(!answerQuestions.isEmpty())redisStore.addAnswerQuestion(answerQuestions.toArray(new String[0]));
			if(!notAnswerQuestions.isEmpty())redisStore.addNotAnswerQuestion(notAnswerQuestions.toArray(new String[0]));
		}
		
	}
	
	/**
	 * 创建page
	 * @param pageStr
	 * @return
	 */
	private Page createPage(String pageStr){
		@SuppressWarnings("unchecked")
		Map<String, Object> pageInfo =JSON.parseObject(pageStr, Map.class);
		Page page = new Page();
        page.setRawText((String)pageInfo.get("respBody"));
        page.setUrl(new PlainText((String)pageInfo.get("url")));
        page.setRequest(JSONObject.toJavaObject((JSON)pageInfo.get("request"), Request.class));
        page.setStatusCode((Integer)pageInfo.get("statusCode"));
        return page;
	}
	
	/**
	 * 
	 * @param html
	 */
	private void createNextRequest(String curPage){
		logger.info(curPage);
		String url = curPage.substring(0,curPage.indexOf("=")+1);
		url +=  (Long.parseLong(curPage.substring(curPage.indexOf("=")+1))+1);
		redisStore.addRequest(url);
	}
	

}
