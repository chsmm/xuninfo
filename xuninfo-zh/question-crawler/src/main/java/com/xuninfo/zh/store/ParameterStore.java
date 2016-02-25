package com.xuninfo.zh.store;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

@Component
public class ParameterStore {
	
	private  final ConcurrentLinkedQueue<String> offsets = new ConcurrentLinkedQueue<String>();
	
	public boolean isEmpty(){
		return offsets.isEmpty();
	}
	
	public void put(String offset){
		offsets.offer(offset);
	}
	
	public Map<String, String> poll(){
		String offset = offsets.poll();
		if(StringUtils.isNotBlank(offset)){
			Map<String, String> map  = Maps.newHashMapWithExpectedSize(4);
			map.put("start", "0");
			map.put("offset", offset);
			map.put("_xsrf", "");
			return map;
		}
		return null;
		
	}
	
}
