package com.xuninfo.zh.crawler.Pipeline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xuninfo.zh.store.RedisStore;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
@Component
public class RedisPipeline implements Pipeline {

	@Autowired
	RedisStore redisStore;
	
	public void process(ResultItems resultItems, Task task) {

	}

}
