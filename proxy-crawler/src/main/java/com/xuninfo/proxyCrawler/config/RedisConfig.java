package com.xuninfo.proxyCrawler.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.xuninfo.proxyCrawler.serializer.JsonRedisSerializer;
import com.xuninfo.proxyCrawler.serializer.KryoRedisSerializer;
@Configuration
@EnableCaching
public class RedisConfig {
	
	@Bean
	public CacheManager cacheManager(RedisTemplate<String, String> redisTemplate) {
		return new RedisCacheManager(redisTemplate);
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate(
			RedisConnectionFactory factory) {
		final RedisTemplate<String, String>  template = new RedisTemplate<String, String>();
		template.setConnectionFactory(factory);
		RedisSerializer<String>  serializer = new KryoRedisSerializer();
		RedisSerializer<String>  jsonSerializer = new JsonRedisSerializer();
		template.setDefaultSerializer(jsonSerializer);
		template.setValueSerializer(serializer);
		return template;
	}
}
