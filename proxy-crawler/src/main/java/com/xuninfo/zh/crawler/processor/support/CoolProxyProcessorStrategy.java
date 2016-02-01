package com.xuninfo.zh.crawler.processor.support;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.xuninfo.zh.crawler.processor.ProcessorStrategy;
@Component
public class CoolProxyProcessorStrategy implements ProcessorStrategy {

	public List<String> processor(Html html,String url) {
		List<String> hosts = Lists.newArrayList();
		List<Selectable> trs  = html.xpath("div[@id=\"container\"]/div[@id=\"main\"]/table/tbody/tr").nodes();
		trs = trs.subList(1, trs.size());
		trs.set(5, null);
		for(Selectable tr : trs){
			if(null==tr)continue;
			String tdContent = tr.xpath("tr/td[1]/html()").get();
			if(StringUtils.isEmpty(tdContent))continue;
			String ip =tdContent.substring(tdContent.lastIndexOf('"', tdContent.lastIndexOf('"')-1)+1,tdContent.lastIndexOf('"'));
			byte[] chs = ip.getBytes();
			for(int i = 0;i < chs.length;i++){  
				byte ch = chs[i];
	            if(ch >= 97 && ch <= 122){  
	            	ch =  (byte) (97 + (ch - 97 + 13) % 26);  
	            }  
	            else if(ch >= 65 && ch <= 90){  
	            	ch = (byte) (65 + (ch - 65 + 13) % 26);  
	            }
	            chs[i] = ch;
	        }
			
			ip = new String(Base64.decodeBase64(chs));
			hosts.add(Joiner.on(":").join(ip,tr.xpath("tr/td[2]/text()"),url));
		}
		return hosts;
	}

}
