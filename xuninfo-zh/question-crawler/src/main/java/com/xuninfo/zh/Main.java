package com.xuninfo.zh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.xuninfo.zh.crawler.impl.QuestionsCrawler;
import com.xuninfo.zh.crawler.support.ExecutorServiceUtil;
/**
 * 启动类
 * @author ch
 * 
 *
 */
@SpringBootApplication
public class Main {
	
	public static void main(String[] args) {
		QuestionsCrawler crawler = SpringApplication.run(Main.class, args).getBean(QuestionsCrawler.class);
		crawler.setExecutorService(ExecutorServiceUtil.getExecutorService());
		crawler.startAsync();
	}

}
