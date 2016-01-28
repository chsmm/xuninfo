package com.xuninfo.zh.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisStore {
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	volatile boolean  is=false;
	public String getProxy(){
		if(!is){
			is=true;
			redisTemplate.opsForSet().add("httpProxy","91.142.159.235:80","186.229.16.154:80");
		}
		return redisTemplate.opsForSet().randomMember("httpProxy");
	
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
	
	public void addAnswerQuestion(String ...answerQuestion){
		redisTemplate.opsForSet().add("answerQuestions",answerQuestion);
	}
	
	public void addNotAnswerQuestion(String ...notAnswerQuestions){
		redisTemplate.opsForSet().add("notAnswerQuestions",notAnswerQuestions);
	}

}
