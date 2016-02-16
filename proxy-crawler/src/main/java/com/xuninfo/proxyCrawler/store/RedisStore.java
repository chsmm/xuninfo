package com.xuninfo.proxyCrawler.store;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisStore {
	
	private Logger logger  = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	
	public void addHttpProxy(String... httpProxy){
		redisTemplate.opsForSet().add("httpProxy",httpProxy);
	}
	
	public void removeHttpProxy(String httpProxy){
		redisTemplate.opsForSet().remove("httpProxy", httpProxy);
	}
	
	public void addVerifyHttpProxy(String httpProxy){
		try {
			redisTemplate.opsForSet().add("verifyHttpProxy", httpProxy);
		} catch (Exception e) {
			logger.error("插入 verifyHttpProxy:"+httpProxy+"异常", e);
		}	
	}
	
	public String getValidProxy(){
		try {
			return redisTemplate.opsForSet().randomMembers("verifyHttpProxy", 1).get(0);
		} catch (Exception e) {
			logger.error("获取 verifyHttpProxy异常", e);
		}
		return  null;
	}
	
	public List<String> getHttpProxys(){
		Long size = redisTemplate.opsForSet().size("verifyHttpProxy");
		return size<200?redisTemplate.opsForSet().randomMembers("httpProxy", 40):null;
	}
	
	public void addFailedRequest(String failedRequestInfo){
		redisTemplate.opsForSet().add("httpProxyFailedRequest",failedRequestInfo);
	}
	
}
