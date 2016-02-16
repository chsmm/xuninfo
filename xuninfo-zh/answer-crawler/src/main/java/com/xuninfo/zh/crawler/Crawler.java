package com.xuninfo.zh.crawler;

import java.util.concurrent.ExecutorService;

import com.google.common.util.concurrent.Service;

public interface Crawler extends Service{
	
	public void setExecutorService(ExecutorService executorService);
	
	public ExecutorService getExecutorService();

}
