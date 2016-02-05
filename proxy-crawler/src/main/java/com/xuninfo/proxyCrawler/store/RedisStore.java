package com.xuninfo.proxyCrawler.store;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;

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
	
	
	enum index{
		A, B ,C, D, E, F, G, H, I, J, K, L, M, N ,O ,P, Q ,R, S, T, U, V, W, X, Y, Z
	}
	
	public void addVerifyHttpProxy(String httpProxy){
		try {
			String time = DateTime.now().toString("yyyyMMddhhmmss");
			boolean succeed=false;
			for(index i : index.values()){
				String name  = Joiner.on("-").join("httpProxy",time,i.name());
				if(!succeed && !redisTemplate.opsForHash().hasKey("verifyHttpProxy",name)){
					succeed = redisTemplate.opsForHash().putIfAbsent("verifyHttpProxy", name, httpProxy);
				};
			}	
		} catch (Exception e) {
			logger.error("插入 verifyHttpProxy:"+httpProxy+"异常", e);
		}	
	}
	
	public String getValidProxy(){
		try {
			long time = Long.parseLong(DateTime.now().toString("yyyyMMddhhmmss"));
			int t=0;
			while((t++)<=3){
				for(index i : index.values()){
					String name  = Joiner.on("-").join("httpProxy",time,i.name());
					Object hostObj =redisTemplate.opsForHash().get("verifyHttpProxy",name);
					if(hostObj!=null)
						return (String)hostObj;
				}
				time=time-(RandomUtils.nextInt(0, 30));
			}
		} catch (Exception e) {
			logger.error("获取 verifyHttpProxy异常", e);
		}
		return  null;
	}
	
	public List<String> getHttpProxys(){
		return redisTemplate.opsForSet().randomMembers("httpProxy", 50);
	}
	
	public void addFailedRequest(String failedRequestInfo){
		redisTemplate.opsForSet().add("httpProxyFailedRequest",failedRequestInfo);
	}
	
}
