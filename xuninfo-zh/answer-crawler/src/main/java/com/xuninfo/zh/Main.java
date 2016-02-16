package com.xuninfo.zh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.xuninfo.zh.crawler.impl.AnswerCrawler;
import com.xuninfo.zh.crawler.support.ExecutorServiceUtil;

@SpringBootApplication
public class Main {

	
	public static void main(String[] args) {
		AnswerCrawler crawler = SpringApplication.run(Main.class, args).getBean(AnswerCrawler.class);
		crawler.setExecutorService(ExecutorServiceUtil.getExecutorService());
		crawler.startAsync();
	}
}
