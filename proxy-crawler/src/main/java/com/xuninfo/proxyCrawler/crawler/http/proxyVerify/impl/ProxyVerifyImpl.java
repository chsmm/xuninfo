package com.xuninfo.proxyCrawler.crawler.http.proxyVerify.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.selector.Html;

import com.xuninfo.proxyCrawler.config.ProxyVerifyConfig;
import com.xuninfo.proxyCrawler.crawler.http.IHandler;
import com.xuninfo.proxyCrawler.crawler.http.proxyVerify.ProxyVerify;
import com.xuninfo.proxyCrawler.crawler.http.support.HttpAsyncClientSupport;
import com.xuninfo.proxyCrawler.store.RedisStore;

@Component("proxyVerify")
public class ProxyVerifyImpl implements ProxyVerify,IHandler{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Site site = Site.me()
	         .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
	         .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
	         .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
			 .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	
	
	@Autowired
	private HttpAsyncClientSupport asyncClientSupport;
	
	@Autowired
	private RedisStore redisStore;
	
	@Autowired
	private ProxyVerifyConfig proxyVerifyConfig;
	
	
	public void doVerify() {
		try {
			//logger.info("Start verify Hosts At Time:"+DateTime.now().toString("yyyy-MM-dd hh:mm:ss"));
			List<String> noVerifyProxyHosts = redisStore.getHttpProxys();
			if(null==noVerifyProxyHosts||noVerifyProxyHosts.isEmpty())return;
			for(String noVerifyProxyHost:noVerifyProxyHosts){
				String[] proxyHosts = noVerifyProxyHost.split(":");
				asyncClientSupport.doGetVerify(proxyVerifyConfig.getHost(), this,new HttpHost(proxyHosts[0], Integer.parseInt(proxyHosts[1]),"http"));
			}
		} catch (Exception e) {
			logger.error("获取验证队列异常 ", e);
		}
	}
	
	public void completed(String url, HttpResponse httpResponse, String respBody) {
		if(StringUtils.isEmpty(respBody))return;
		Html html = Html.create(respBody);
		String feature =html.xpath("title/text()").get();
		if(!StringUtils.isEmpty(feature)&&proxyVerifyConfig.getFeature().equals(feature)){
			redisStore.addVerifyHttpProxy(asyncClientSupport.getProxyHost());
			redisStore.removeHttpProxy(asyncClientSupport.getProxyHost());
			
		}
	}
	
	public void failed(String url, Exception exception) {
		try{
			redisStore.removeHttpProxy(asyncClientSupport.getProxyHost());
		}catch(Exception e){
			logger.error("处理失败url异常", exception);
		}
	}

	public void cancelled(String url) {
		try{
			redisStore.removeHttpProxy(asyncClientSupport.getProxyHost());
		}catch(Exception e){
			logger.error("处理取消url异常", e);
		}
	}


	
	
	
}
