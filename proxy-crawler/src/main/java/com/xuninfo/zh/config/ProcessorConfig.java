package com.xuninfo.zh.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Component
@ConfigurationProperties(prefix="strategy",locations="config/processorConfig.yml")
public class ProcessorConfig {
	
	private Map<String, String> processorStrategys;

	public Map<String, String> getProcessorStrategys() {
		return processorStrategys;
	}

	public void setProcessorStrategys(Map<String, String> processorStrategys) {
		this.processorStrategys = processorStrategys;
	}

	
	
	
}
