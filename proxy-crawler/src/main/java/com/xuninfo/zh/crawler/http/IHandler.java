package com.xuninfo.zh.crawler.http;

import org.apache.http.HttpResponse;

import us.codecraft.webmagic.Request;

public interface IHandler {
	/** 
     * 处理异常时，执行该方法 
     * @return 
     */  
	void failed(Request request,Exception e);  
      
    /** 
     * 处理正常时，执行该方法 
     * @return 
     */  
	void completed(Request request, HttpResponse httpResponse, String respBody);
      
    /** 
     * 处理取消时，执行该方法 
     * @return 
     */  
	void cancelled(Request request);
}
