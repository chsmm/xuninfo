package com.xuninfo.zh.store;

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
	
	public String getValidProxy(){
		try {
			return redisTemplate.opsForSet().randomMembers("verifyHttpProxy", 1).get(0);
		} catch (Exception e) {
			logger.error("获取 verifyHttpProxy异常", e);
		}
		return  null;
	}
	
	public void removeProxy(String proxy){
		redisTemplate.opsForSet().remove("verifyHttpProxy", proxy);
	}
	
	public void addFailedRequest(String failedRequestInfo){
		redisTemplate.opsForSet().add("failedRequest",failedRequestInfo);
	}
	
	public void addPage(String page){
		redisTemplate.opsForSet().add("pages",page);
	}
	
	public String getPage(){
		return redisTemplate.opsForSet().pop("pages");
	}
	
	public void addRequest(String request){
		redisTemplate.opsForSet().add("request",request);
	}
	
	public String getRequest(){
		return redisTemplate.opsForSet().pop("request");
	}
	
	public long getRequestLen(){
		return redisTemplate.opsForSet().size("request");
	}
	
	public void addQuestions(String ...answerQuestion){
		redisTemplate.opsForSet().add("questions",answerQuestion);
	}
	

}
