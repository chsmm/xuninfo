package com.xuninfo.zh.crawler.http.support;

import java.nio.charset.CodingErrorAction;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import com.google.common.collect.Lists;


public class HttpClientGenerator {
	private CloseableHttpClient httpclient = null;

    public HttpClientGenerator(int maxPerRoute,int maxTotal,Map<String, String> headerMaps) {
        List<Header> headers = Lists.newArrayList();
        for (Entry<String, String> header : headerMaps.entrySet()) {
        	headers.add(new BasicHeader(header.getKey(), header.getValue()));
		}
        init(maxPerRoute,maxTotal,headers);
    }
    
    private void init(int maxPerRoute,int maxTotal,List<Header> headers){
    	try {
	        MessageConstraints messageConstraints = MessageConstraints.custom()
	                .setMaxHeaderCount(200)
	                .setMaxLineLength(2000)
	                .build();
	           // Create connection configuration
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8).setMessageConstraints(messageConstraints).build();
            httpclient = HttpClients.custom().
	    		   setDefaultSocketConfig(SocketConfig.custom().setTcpNoDelay(true).build()).
	    		   setDefaultConnectionConfig(connectionConfig).
	    		   setMaxConnPerRoute(maxPerRoute)
	    		   .setMaxConnTotal(maxTotal)
	    		   .setDefaultHeaders(headers)
	    		   .build();
		} catch (Exception e) {
			//logger.error("初始化 HttpAsyncClients 失败", e);
		}
    }

    public CloseableHttpClient getClient() {
        return httpclient;
    }
}
