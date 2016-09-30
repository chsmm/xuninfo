package com.xuninfo.proxyCrawler.store;

import java.util.Set;


public interface Store<K,V> {
	
	
	void put(K k,V v);
	
	void putAll(K k, V[] v);
	
	V get(K k);
	
	V pop(K k);
	
	Set<V> pop(K k,int count);
	
	
	
	void delete(K k);

}
