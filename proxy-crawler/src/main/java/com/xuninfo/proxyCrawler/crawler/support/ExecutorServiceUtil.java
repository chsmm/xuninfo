package com.xuninfo.proxyCrawler.crawler.support;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorServiceUtil {
	
	private static final ExecutorService service;
	
	static{
		ThreadPoolExecutor	tmepservice =(ThreadPoolExecutor) Executors.newFixedThreadPool(100);
		tmepservice.setKeepAliveTime(60, TimeUnit.SECONDS);
		tmepservice.setRejectedExecutionHandler(new AwaitPolicy());
		tmepservice.setThreadFactory(new ThreadFactory() {
			private AtomicInteger atomicInteger = new AtomicInteger(0);
			public Thread newThread(Runnable r) {
				return new Thread(r, "proxyCrawler--exc--"+atomicInteger.incrementAndGet());
			}
		});
		service = tmepservice;
	}
	
	public static ExecutorService getExecutorService(){
		return service;
	}
	
	 public static class AwaitPolicy implements RejectedExecutionHandler {
	        /**
	         * Creates an {@code AbortPolicy}.
	         */
	        public AwaitPolicy() { }

	        /**
	         * Always throws RejectedExecutionException.
	         *
	         * @param r the runnable task requested to be executed
	         * @param e the executor attempting to execute this task
	         * @throws RejectedExecutionException always.
	         */
	        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
	            try {
					e.getQueue().offer(r, 600, TimeUnit.SECONDS);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
	        }
	    }

}
