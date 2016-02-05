package com.xuninfo.zh.store;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Page;

@Component
public class PageStore {
	
	private  final ConcurrentLinkedQueue<Page> pages = new ConcurrentLinkedQueue<Page>();
	
	private  final ConcurrentLinkedQueue<String> pageUrl = new ConcurrentLinkedQueue<String>();
	
	public void addPage(Page page){
		pages.add(page);
	}
	
	public Page getPage(){
		return pages.poll();
	}
	
	public void addPageUrl(String url){
		pageUrl.add(url);
	}
	
	public String getPageUrl(){
		return pageUrl.poll();
	}
	
	public boolean urlIsEmpty(){
		return pageUrl.isEmpty();
	}
	
	public boolean pageIsEmpty(){
		return pages.isEmpty();
	}

}
