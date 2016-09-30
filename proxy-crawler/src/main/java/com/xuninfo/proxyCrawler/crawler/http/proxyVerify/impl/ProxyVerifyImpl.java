package com.xuninfo.proxyCrawler.crawler.http.proxyVerify.impl;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.selector.Html;

import com.xuninfo.proxyCrawler.config.ProxyVerifyConfig;
import com.xuninfo.proxyCrawler.crawler.http.IHandler;
import com.xuninfo.proxyCrawler.crawler.http.proxyVerify.ProxyVerify;
import com.xuninfo.proxyCrawler.crawler.http.support.HttpAsyncClientSupport;
import com.xuninfo.proxyCrawler.store.GuavaStore;

@Component("proxyVerify")
public class ProxyVerifyImpl implements ProxyVerify,IHandler{
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	
	@Autowired
	private HttpAsyncClientSupport asyncClientSupport;
	

	
	@Autowired
	GuavaStore<String, String> guavaStore;
	
	@Autowired
	private ProxyVerifyConfig proxyVerifyConfig;
	
	
	public void doVerify() {
		try {
			//logger.info("Start verify Hosts At Time:"+DateTime.now().toString("yyyy-MM-dd hh:mm:ss"));
			//guavaStore.pop("proxys", 20);
			Set<String> noVerifyProxyHosts = guavaStore.pop("proxys", 20);;
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
		if(!StringUtils.isEmpty(feature) && proxyVerifyConfig.getFeature().equals(feature)){
			//redisStore.addVerifyHttpProxy(asyncClientSupport.getProxyHost());
			guavaStore.put("validProxy", asyncClientSupport.getProxyHost());
			//redisStore.removeHttpProxy(asyncClientSupport.getProxyHost());
			
		}
	}
	
	public void failed(String url, Exception exception) {
		
	}

	public void cancelled(String url) {	
	}


	
	
	
}
