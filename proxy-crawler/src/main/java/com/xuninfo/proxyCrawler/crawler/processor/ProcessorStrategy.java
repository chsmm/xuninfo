package com.xuninfo.proxyCrawler.crawler.processor;

import java.util.List;

import us.codecraft.webmagic.selector.Html;

public interface ProcessorStrategy {
	public List<String> processor(Html html,String url);
}
