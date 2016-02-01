package com.xuninfo.zh.crawler.http.proxyVerify.impl;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.HostAndPort;
import us.codecraft.webmagic.Site;

import com.xuninfo.zh.config.ProxyVerifyConfig;
import com.xuninfo.zh.crawler.http.IHandler;
import com.xuninfo.zh.crawler.http.proxyVerify.ProxyVerify;
import com.xuninfo.zh.crawler.http.support.HttpAsyncClientSupport;
import com.xuninfo.zh.store.RedisStore;

@Component("proxyVerify")
public class ProxyVerifyImpl implements ProxyVerify,IHandler,InitializingBean{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Site site = Site.me()
	         .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
	         .setCharset("UTF-8")
	         .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
	         .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
			 .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	
	private LinkedBlockingQueue<String> verifyHostsQueue;
	
	@Autowired
	private HttpAsyncClientSupport asyncClientSupport;
	
	@Autowired
	private RedisStore redisStore;
	
	@Autowired
	private ProxyVerifyConfig proxyVerifyConfig;
	
	public ProxyVerifyImpl() {
		
	}
	
	public void addVerifyQueue(List<String> proxyHosts){
		for(String proxyHost : proxyHosts){
			try {
				verifyHostsQueue.offer(proxyHost, proxyVerifyConfig.getVerifyQueueTimeout(), TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				logger.error("添加验证队列异常 "+proxyHost, e);
			}
		}
	}
	
	public void doVerify() {
		try {
			while(!verifyHostsQueue.isEmpty()){
				String proxyHost = verifyHostsQueue.poll( proxyVerifyConfig.getVerifyQueueTimeout(), TimeUnit.SECONDS);
				String[] proxyHosts = proxyHost.split(":");
				asyncClientSupport.doGetVerify(proxyVerifyConfig.getHost(), site.getHeaders(), this,new HttpHost(proxyHosts[0], Integer.parseInt(proxyHosts[1]),"http"));
			}
		} catch (InterruptedException e) {
			logger.error("获取验证队列异常 ", e);
		}
	}
	
	public void completed(String url, HttpResponse httpResponse, String respBody) {
		logger.info(respBody);
	}
	
	public void failed(String url, Exception e) {
		
	}

	public void cancelled(String url) {
		
	}

	public void afterPropertiesSet() throws Exception {
		verifyHostsQueue = new LinkedBlockingQueue<String>(proxyVerifyConfig.getVerifyQueueSize());
	}
	
	
	
}
