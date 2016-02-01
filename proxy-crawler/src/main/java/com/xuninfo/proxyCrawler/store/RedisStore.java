package com.xuninfo.proxyCrawler.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisStore {
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	volatile boolean  is=false;
	public String getProxy(){
		/*if(!is){
			is=true;
			redisTemplate.opsForSet().add("httpProxy","91.142.159.235:80","186.229.16.154:80");
		}*/
		return redisTemplate.opsForSet().randomMember("httpProxy");
	
	}
	
	public void addHttpProxy(String... httpProxy){
		redisTemplate.opsForSet().add("httpProxy",httpProxy);
	}
	
	public void addHttpProxyPage(String httpProxyPage){
		redisTemplate.opsForSet().add("httpProxyPages",httpProxyPage);
	}
	
	public String getHttpProxyPage(){
		return redisTemplate.opsForSet().pop("httpProxyPages");
	}
	
	public long getHttpProxyLen(){
		return redisTemplate.opsForSet().size("httpProxyPages");
	}
	
	public void addFailedRequest(String failedRequestInfo){
		redisTemplate.opsForSet().add("httpProxyFailedRequest",failedRequestInfo);
	}
}
