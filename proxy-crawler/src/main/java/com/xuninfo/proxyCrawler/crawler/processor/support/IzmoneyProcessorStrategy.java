package com.xuninfo.proxyCrawler.crawler.processor.support;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.xuninfo.proxyCrawler.crawler.processor.ProcessorStrategy;
@Component
public class IzmoneyProcessorStrategy implements ProcessorStrategy{
	public List<String> processor(Html html,String url) {
		List<String> hosts = Lists.newArrayList();
		List<Selectable> trs  = html.xpath("div[@id=\"wrap\"]//div[@class=\"block-settings\"]/table[@id=\"proxylisttable\"]/tbody/tr").nodes();
		if (!trs.isEmpty()) {
			trs = trs.subList(2, trs.size());
			StringBuilder builder;
			for (Selectable tr : trs) {
				builder = new StringBuilder();
				List<String> els = tr.xpath("tr/td[@class=\"ip\"]/*").all();
				for (int i = 0; i < els.size(); i++) {
					String ip = els.get(i);
					if (StringUtils.isEmpty(ip) || ip.indexOf("none") != -1)
						continue;
					int start = ip.indexOf(">");
					start = start == -1 ? 0 : start + 1;
					int end = ip.indexOf("/");
					end = end == -1 ? 0 : end - 1;
					if (end == start)
						continue;
					ip = ip.substring(start, end);
					if (!StringUtils.isEmpty(ip)) {
						builder.append(ip.trim());
					}
				}
				hosts.add(Joiner.on(":").join(builder.toString(),
						tr.xpath("tr/td[@class=\"port\"]/text()").get(), url));
			}
		}
		return hosts;
	}

}
