package com.xuninfo.proxyCrawler.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.alibaba.fastjson.JSON;

public class JsonRedisSerializer implements RedisSerializer<String> {
	
	

	public byte[] serialize(String value) throws SerializationException {
		return JSON.toJSONBytes(value);
	}

	public String deserialize(byte[] bytes) throws SerializationException {
		return (String) JSON.parse(bytes);
	}

	

}
