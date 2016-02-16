package com.xuninfo.zh.crawler.http.support;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.xuninfo.zh.config.CrawlerConfig;
import com.xuninfo.zh.config.HttpConfig;
import com.xuninfo.zh.crawler.http.IHandler;
import com.xuninfo.zh.store.RedisStore;

@Component
public class HttpAsyncClientSupport implements InitializingBean,DisposableBean{
	
protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RedisStore redisStore;
	
	@Autowired
	private CrawlerConfig crawlerConfig;
	
	@Autowired
	private ProxyVerify proxyVerify;
	
	private CloseableHttpAsyncClient httpAsyncClient;
	
	private AtomicBoolean isNewProxy = new AtomicBoolean(true);
	
	//ConcurrentMap<String, HttpHost> proxryPool = Maps.newConcurrentMap();
	
	Table<String, HttpHost, AtomicInteger> proxryPoolTable =  HashBasedTable.create();
	
	
	private void init(){
		try {
			HttpConfig httpConfig = crawlerConfig.getHttpConfig();
	        //配置io线程  
	    			IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
	    	                .setConnectTimeout(httpConfig.getConnectTimeout())
	    	                .setSoTimeout(httpConfig.getConnectTimeout())
	    	                .setIoThreadCount(30)
	    	                .build();  
	       /* List<Header> headers = Lists.newArrayList();
	        for (Entry<String, String> header : httpConfig.getHeaders().entrySet()) {
	        	headers.add(new BasicHeader(header.getKey(), header.getValue()));
			}*/
	        MessageConstraints messageConstraints = MessageConstraints.custom()
	                .setMaxHeaderCount(200)
	                .setMaxLineLength(2000)
	                .build();
	            // Create connection configuration
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Charset.forName(httpConfig.getEncoding())).setMessageConstraints(messageConstraints).build();
	       httpAsyncClient = HttpAsyncClients.custom().
	    		   setDefaultIOReactorConfig(ioReactorConfig).
	    		   setDefaultConnectionConfig(connectionConfig)
	    		   .setMaxConnTotal(httpConfig.getConnectMaxTotal())
	    		   //.setDefaultHeaders(headers)
	    		   .build();
		} catch (Exception e) {
			logger.error("初始化 HttpAsyncClients 失败", e);
		}
	}

	public void afterPropertiesSet() throws Exception {
		init();
		if(httpAsyncClient!=null){
			httpAsyncClient.start();
		}
		
	}

	public void destroy() throws Exception {
		if(httpAsyncClient!=null){
			httpAsyncClient.close();
		}
	}

	
	
	public void doGet(final String url, final IHandler handler) {
		try {
			final HttpConfig httpConfig = crawlerConfig.getHttpConfig();
			
			// 设置url与请求头信息
			RequestBuilder requestBuilder = RequestBuilder.get().setUri(url);
			for (Map.Entry<String, String> headerEntry : httpConfig.getHeaders().entrySet()) {
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
			// 配置访问限制
			RequestConfig.Builder requestConfigBuilder = RequestConfig
					.custom()
					.setConnectionRequestTimeout(httpConfig.getConnectTimeout())
					.setSocketTimeout(httpConfig.getConnectTimeout())
					.setConnectTimeout(httpConfig.getConnectTimeout());

			if (httpConfig.getProxy()) {
				HttpHost httpHost = getProxy();
				requestConfigBuilder.setProxy(httpHost);
			}
			// 生成request
			HttpUriRequest httpUriRequest = requestBuilder.setConfig(requestConfigBuilder.build()).build();
			httpAsyncClient.execute(httpUriRequest,
					new FutureCallback<HttpResponse>() {

						public void failed(Exception exception) {
							removeProxy();
							handler.failed(url, exception);
						}

						public void completed(final HttpResponse resp) {
							try {
								
								int statusCode = resp.getStatusLine().getStatusCode();
								if (statusCode!=200){
									removeProxy();
									//logger.warn("HttpResponse StatusCode:"+statusCode);
									failed(new RuntimeException("HttpResponse StatusCode:"+statusCode));
									return;
								}
								HttpEntity entity = resp.getEntity();
								if (entity != null) {
						             if (entity.getContentEncoding() != null) {
					                    if ("gzip".equalsIgnoreCase(entity.getContentEncoding().getValue())) {
					                    	entity = new GzipDecompressingEntity(entity);
					                    } else if ("deflate".equalsIgnoreCase(entity.getContentEncoding().getValue())) {
					                    	entity = new DeflateDecompressingEntity(entity);
					                    }  
						            }
									InputStream instream = entity.getContent();
									try {
										String content = IOUtils.toString(new BufferedReader(new InputStreamReader(instream,httpConfig.getEncoding())));
										handler.completed(url, resp,content);
									} finally {
										logger.info("download "+url);
										instream.close();
										EntityUtils.consume(entity);
									}
								}
							} catch (Exception e) {
								removeProxy();
								handler.failed(url, e);
							}
						}

						public void cancelled() {
							removeProxy();
							handler.cancelled(url);
						}
					});
		} catch (Exception exception) {
			handler.failed(url, exception);
			logger.warn(url + "--异常:"+exception.getMessage());
		}
	}
	
	
	
	public String doPost(){
		return null;
	}
	
	private HttpHost getHost(){
		HttpHost httpHost = null;
		String hostStr;
		do{
			hostStr = redisStore.getValidProxy();
		}while(StringUtils.isEmpty(hostStr));
		String[] hp=hostStr.split(":");
		httpHost =  new HttpHost(hp[0], Integer.parseInt(hp[1]));
		proxryPoolTable.put("proxy", httpHost,  new AtomicInteger(0));
		return httpHost;
	}
	
	private HttpHost getProxy(){
		for(;;){
			if(isNewProxy.compareAndSet(true, false)){
				return getHost();
			}else{
				Map<HttpHost, AtomicInteger> proxyMap;
				do{
					proxyMap  = proxryPoolTable.row("proxy");
				}while(proxyMap.isEmpty());
				
				for(Entry<HttpHost, AtomicInteger> entry : proxyMap.entrySet()){
					AtomicInteger count = entry.getValue();
					if(count.get()>15){
						changeProxy();
						return getProxy();
					}else{
						count.incrementAndGet();
						return entry.getKey();
					}
				}
			}
		}
	}
	
	
	
	
	private void removeProxy(){
		Map<HttpHost, AtomicInteger> proxyMap = proxryPoolTable.row("proxy");
		for(Entry<HttpHost, AtomicInteger> entry : proxyMap.entrySet()){
			redisStore.removeProxy(entry.getKey().toHostString());
			break;
		}
		changeProxy();
	}
	
	private void changeProxy(){
		proxryPoolTable.clear();
		isNewProxy.getAndSet(true);
	}
	
	
	
	
}
