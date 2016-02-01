package com.xuninfo.proxyCrawler.crawler.http.proxyVerify;

import java.util.List;


public interface ProxyVerify {
	
	public void addVerifyQueue(List<String> proxyHosts);
}
