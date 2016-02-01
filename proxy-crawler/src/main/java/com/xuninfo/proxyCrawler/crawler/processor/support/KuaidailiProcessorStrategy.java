package com.xuninfo.proxyCrawler.crawler.processor.support;

import java.util.List;

import org.springframework.stereotype.Component;

import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.xuninfo.proxyCrawler.crawler.processor.ProcessorStrategy;
@Component
public class KuaidailiProcessorStrategy implements ProcessorStrategy {

	public List<String> processor(Html html,String url) {
		List<String> hosts = Lists.newArrayList();
		List<Selectable> trs  = html.xpath("div[@id=\"container\"]//table[@class=\"table\"]/tbody/tr").nodes();
		for(Selectable tr : trs){
			hosts.add(Joiner.on(":").join(tr.xpath("tr/td[1]/text()").get(),tr.xpath("tr/td[2]/text()"),url));
		}
		return hosts;
	}

}
