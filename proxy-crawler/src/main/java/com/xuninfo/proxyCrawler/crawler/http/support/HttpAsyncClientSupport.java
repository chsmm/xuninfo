package com.xuninfo.proxyCrawler.crawler.http.support;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xuninfo.proxyCrawler.config.CrawlerConfig;
import com.xuninfo.proxyCrawler.config.HttpConfig;
import com.xuninfo.proxyCrawler.crawler.http.IHandler;
import com.xuninfo.proxyCrawler.store.RedisStore;

@Component
public class HttpAsyncClientSupport implements InitializingBean,DisposableBean{
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RedisStore redisStore;
	
	@Autowired
	private CrawlerConfig crawlerConfig;
	
	
	private CloseableHttpAsyncClient httpAsyncClient;
	
	
	private void init(){
		try {
			HttpConfig httpConfig = crawlerConfig.getHttpConfig();
			//绕过证书验证，处理https请求  
			SSLContext sslcontext = SSLContexts.createSystemDefault();
			HostnameVerifier hostnameVerifier = new DefaultHostnameVerifier();  
	        // 设置协议http和https对应的处理socket链接工厂的对象  
			Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create()
		            .register("http", NoopIOSessionStrategy.INSTANCE)
		            .register("https", new SSLIOSessionStrategy(sslcontext,hostnameVerifier))
		            .build();
	        //配置io线程  
			IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
	                .setIoThreadCount(Runtime.getRuntime().availableProcessors())
	                .setConnectTimeout(httpConfig.getConnectTimeout())
	                .setSoTimeout(httpConfig.getConnectTimeout())
	                .build();  
	        //设置连接池大小    
	        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);  
	        PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(ioReactor, null, sessionStrategyRegistry, null);
	        connectionManager.setMaxTotal(httpConfig.getConnectMaxTotal());
	        httpAsyncClient = HttpAsyncClients.custom().setConnectionManager(connectionManager).build();
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

	
	
	public void doGet(final String url,final Map<String, String> headers,final IHandler handler){
		final HttpConfig httpConfig = crawlerConfig.getHttpConfig();
		//设置url与请求头信息
		RequestBuilder requestBuilder = RequestBuilder.get().setUri(url);
		if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }
		//配置访问限制
		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
	                .setConnectionRequestTimeout(httpConfig.getConnectTimeout())
	                .setSocketTimeout(httpConfig.getConnectTimeout())
	                .setConnectTimeout(httpConfig.getConnectTimeout());
		
		if(httpConfig.getProxy()){
			HttpHost hots = getProxy();
			if(hots!=null){
				requestConfigBuilder.setProxy(hots);
			}
		}    
		//生成request
		HttpUriRequest httpUriRequest = requestBuilder.setConfig(requestConfigBuilder.build()).build();
		httpAsyncClient.execute(httpUriRequest, new FutureCallback<HttpResponse>() {
			
			public void failed(Exception exception) {
				handler.failed(url,exception);
			}
			
			public void completed(final HttpResponse resp) {
                //这里使用EntityUtils.toString()方式时会大概率报错，原因：未接受完毕，链接已关  
                try {  
                	
                    HttpEntity entity = resp.getEntity();  
                    if (entity != null) {  
                        final InputStream instream = entity.getContent();  
                        try {   
                            String content = IOUtils.toString(new BufferedReader(new InputStreamReader(instream,httpConfig.getEncoding())));
                        	if(httpConfig.getRetryStatusCode().contains(resp.getStatusLine().getStatusCode()))
                        		return;
                        	else if(httpConfig.getRetryStatusCode().contains(resp.getStatusLine().getStatusCode()))
                        		failed(new RuntimeException(content));
                        	else 
                        		handler.completed(url,resp,content);
                        	
                        } finally {  
                            instream.close();  
                            EntityUtils.consume(entity);  
                        }  
                    }  
                } catch (Exception e) { 
                	handler.failed(url,e);
                }  
			}
			
			public void cancelled() {
				handler.cancelled(url);
			}
		});
	}
	
	public void doGetVerify(final String url,final Map<String, String> headers,final IHandler handler,HttpHost proxyHost){
		final HttpConfig httpConfig = crawlerConfig.getHttpConfig();
		//设置url与请求头信息
		RequestBuilder requestBuilder = RequestBuilder.get().setUri(url);
		if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }
		//配置访问限制
		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
	                .setConnectionRequestTimeout(httpConfig.getConnectTimeout())
	                .setSocketTimeout(httpConfig.getConnectTimeout())
	                .setConnectTimeout(httpConfig.getConnectTimeout())
	                .setProxy(proxyHost);
	   
		//生成request
		HttpUriRequest httpUriRequest = requestBuilder.setConfig(requestConfigBuilder.build()).build();
		httpAsyncClient.execute(httpUriRequest, new FutureCallback<HttpResponse>() {
			
			public void failed(Exception exception) {
				handler.failed(url,exception);
			}
			
			public void completed(final HttpResponse resp) {
                //这里使用EntityUtils.toString()方式时会大概率报错，原因：未接受完毕，链接已关  
                try {  
                	
                    HttpEntity entity = resp.getEntity();  
                    if (entity != null) {  
                        final InputStream instream = entity.getContent();  
                        try {   
                            String content = IOUtils.toString(new BufferedReader(new InputStreamReader(instream,httpConfig.getEncoding())));
                        	if(httpConfig.getRetryStatusCode().contains(resp.getStatusLine().getStatusCode()))
                        		return;
                        	else if(httpConfig.getRetryStatusCode().contains(resp.getStatusLine().getStatusCode()))
                        		failed(new RuntimeException(content));
                        	else 
                        		handler.completed(url,resp,content);
                        	
                        } finally {  
                            instream.close();  
                            EntityUtils.consume(entity);  
                        }  
                    }  
                } catch (Exception e) { 
                	handler.failed(url,e);
                }  
			}
			
			public void cancelled() {
				handler.cancelled(url);
			}
		});
	}
	
	
	public String doPost(){
		return null;
	}
	
	
	
	private HttpHost getProxy(){
		String hostStr = redisStore.getProxy();
		if(StringUtils.isEmpty(hostStr))return null;
		String[] hp=hostStr.split(":");
		HttpHost httpHost =  new HttpHost(hp[0], Integer.parseInt(hp[1]));
		return httpHost;
	}
	
}