package com.xuninfo.proxyCrawler.store;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Component
public class GuavaStore<K,V> implements Store<K,V>{
	
	private  Cache<K,Set<V>> cache = null; 
	
	public GuavaStore() {
		cache = CacheBuilder
        .newBuilder()
        .maximumSize(10000)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build();
	}

	@Override
	public void put(K k, V v) {
		Set<V> values = getValues(k);
		boolean unExit = values==null || values.isEmpty() ;
		
		values = unExit  ? Sets.<V>newConcurrentHashSet() : values;
		values.add(v);
		if(unExit){
			cache.put(k, values);
		}
	}

	@Override
	public void putAll(K k, V[] v) {
		Set<V> values = getValues(k);
		boolean unExit = values==null || values.isEmpty() ;
		values = unExit  ? Sets.<V>newConcurrentHashSet() : values;
		values.addAll(Sets.newHashSet(v));
		if(unExit){
			cache.put(k, values);
		}
	}
	
	@Override
	public V get(K k) {
		try {
			cache.get(k, new Callable<Set<V>>() {
				@Override
				public Set<V> call() throws Exception {
					return Sets.<V>newConcurrentHashSet();
				}
			});
		} catch (ExecutionException e) {
			
		}
		return null;
	}
	
	public Set<V> getValues(final K k) {
		try {
			return cache.get(k, new Callable<Set<V>>() {
				@Override
				public Set<V> call() throws Exception {
					return Sets.<V>newConcurrentHashSet();
				}
			});
		} catch (ExecutionException e) {
		}
		return null;
	}

	
	@Override
	public void delete(K k) {
		cache.invalidate(k);
	}


	@Override
	public Set<V> pop(K k, int count) {
		Set<V> values = getValues(k);
		if( values==null || values.isEmpty() )return Sets.<V>newHashSetWithExpectedSize(0);
		
		boolean isAll = false;
		
		if(count > values.size()) isAll = true;
		
		Set<V> result = Sets.newConcurrentHashSet();
		
		if(isAll){
			result = values ;
			values.clear();
		}else{
			int index = 0;
			Iterator<V> iterator = values.iterator();
			while (iterator.hasNext() && index < count) {
				result.add(iterator.next());
				iterator.remove();
				index++;
			}
		}
		return result;
	}

	@Override
	public V pop(K k) {
		Set<V> values = getValues(k);
		if( values==null || values.isEmpty() )return null;
		Iterator<V> iterator = values.iterator();
		while (iterator.hasNext()) {
			V v  = iterator.next();
			iterator.remove();
			return v;
		}
		return null;
	}

	

	


}
