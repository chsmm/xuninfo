package com.xuninfo.zh.crawler.http;

import java.util.Map;

import org.apache.http.HttpResponse;

public interface IHandler {
	/** 
     * 处理异常时，执行该方法 
     * @return 
     */  
	void failed(Map<String, String> parameters,Exception e);  
      
    /** 
     * 处理正常时，执行该方法 
     * @return 
     */  
	void completed(Map<String, String> parameters, HttpResponse httpResponse, String respBody);
      
    /** 
     * 处理取消时，执行该方法 
     * @return 
     */  
	void cancelled(Map<String, String> parameters);
}
