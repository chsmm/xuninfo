package com.xuninfo.zh.crawler.http;

import org.apache.http.HttpResponse;

public interface IHandler {
	/** 
     * 处理异常时，执行该方法 
     * @return 
     */  
	void failed(String url,Exception e);  
      
    /** 
     * 处理正常时，执行该方法 
     * @return 
     */  
	void completed(String url, HttpResponse httpResponse, String respBody);
      
    /** 
     * 处理取消时，执行该方法 
     * @return 
     */  
	void cancelled(String url);
}
