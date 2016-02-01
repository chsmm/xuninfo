import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.parser.JSONReaderScanner;
import com.alibaba.fastjson.parser.deserializer.JSONObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.xuninfo.zh.serializer.KryoRedisSerializer;


public class KryoTest {
	private static final Kryo KRYO = new Kryo();

	public static void main(String[] args) throws Exception, ExecutionException {
		 CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
	        try {
	            httpclient.start();
	            HttpGet request = new HttpGet("https://www.zhihu.com/topic/19776749/questions?page=1");
	            httpclient.execute(request, new FutureCallback<HttpResponse>() {
					
					private JSONReader jsonReader;

					public void failed(Exception ex) {
						// TODO Auto-generated method stub
						
					}
					
					public void completed(HttpResponse response) {
						
		                    HttpEntity entity = response.getEntity();
		                    InputStream instream =null;
		                    try {
		                    if (entity != null) {  
		                    	instream = entity.getContent();/*
		                    	 String content = IOUtils.toString(new BufferedReader(new InputStreamReader(instream,"utf-8")));
		                         	System.out.println(content);*/
		                    	/*sonReader = new JSONReader(new BufferedReader(new InputStreamReader(instream)));
		                    	jsonReader.startObject();
		                    	String c = jsonReader.readString();
		                    	jsonReader.endObject();
		                    	jsonReader.close();
		                    	System.out.println(c);*/
		                        /*instream = entity.getContent();  
		                        String content = IOUtils.toString(new BufferedReader(new InputStreamReader(instream,"utf-8")));
	                         	System.out.println(content.length());
		                        	System.out.println(Base64.encodeBase64String(IOUtils.toByteArray(new BufferedReader(new InputStreamReader(instream,"utf-8")))).length());*/
		                        	
		                         	System.out.println(new Date(1292799811000L).toLocaleString());
		                        	
		                        
		                    }  
						 } catch (Exception e) { 
	                        	e.printStackTrace();
	                        } finally {   
	                        }  
						
					}
					
					public void cancelled() {
					}
				});
	            System.out.println("xxx");
	}catch(Exception e){
		
	}}
}
