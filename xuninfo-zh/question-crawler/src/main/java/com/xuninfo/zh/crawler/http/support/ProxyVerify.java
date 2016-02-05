package com.xuninfo.zh.crawler.http.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.selector.Html;

import com.xuninfo.zh.config.CrawlerConfig;
import com.xuninfo.zh.config.HttpConfig;
import com.xuninfo.zh.config.ProxyVerifyConfig;

@Component
public class ProxyVerify implements InitializingBean{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CrawlerConfig crawlerConfig;
	
	@Autowired
	ProxyVerifyConfig proxyVerifyConfig;
	
	private HttpClientGenerator httpClientGenerator;
	
	
	public boolean verify(HttpHost host){
		HttpConfig httpConfig = crawlerConfig.getHttpConfig();
		try {
			CloseableHttpResponse httpResponse = httpClientGenerator.getClient().execute(getHttpUriRequest(host));
			if (httpResponse.getStatusLine().getStatusCode()!=200)return false;
			
			HttpEntity entity = httpResponse.getEntity();
			
			if (entity != null) {
				InputStream	instream=null;
				try{
					instream = entity.getContent();
					String content = IOUtils.toString(new BufferedReader(new InputStreamReader(instream,httpConfig.getEncoding())));
					Html html = Html.create(content);
					String feature =html.xpath("title/text()").get();
					if(!StringUtils.isEmpty(feature)&&proxyVerifyConfig.getFeature().equals(feature)){
						return true;
					}
				}finally{
					EntityUtils.consume(entity);
					if(instream!=null)instream.close();
				}	
			}
		}catch(Exception e){
			logger.warn("verify proxy fail:"+e.getMessage());
		}
		return false;
	}
	
	protected HttpUriRequest getHttpUriRequest(HttpHost host) {
        RequestBuilder requestBuilder = RequestBuilder.get().setUri(proxyVerifyConfig.getHost());
        for (Map.Entry<String, String> headerEntry : crawlerConfig.getHttpConfig().getHeaders().entrySet()) {
            requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
        }
        
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectionRequestTimeout(crawlerConfig.getHttpConfig().getConnectTimeout())
                .setSocketTimeout(crawlerConfig.getHttpConfig().getConnectTimeout())
                .setConnectTimeout(crawlerConfig.getHttpConfig().getConnectTimeout())
                .setProxy(host);
        requestBuilder.setConfig(requestConfigBuilder.build());
        return requestBuilder.build();
    }

	public void afterPropertiesSet() throws Exception {
		httpClientGenerator = new HttpClientGenerator(5, 5,crawlerConfig.getHttpConfig().getHeaders());
		
	}
	
	
}
