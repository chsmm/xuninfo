package com.xuninfo.zh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="jdbc",locations="config/jdbc.yml")
public class JDBCConfig {
	
	private String dataSourceClassName;
	private String url;
	private String databaseName;
	private String serverName;
	private String username;
	private String password;
	private String cachePrepStmts;
	private Integer prepStmtsSize;
	private Integer prepStmtsCacheSqlLimit;
	private Boolean userServerPrepStmts;
	
	public String getDataSourceClassName() {
		return dataSourceClassName;
	}
	public void setDataSourceClassName(String dataSourceClassName) {
		this.dataSourceClassName = dataSourceClassName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCachePrepStmts() {
		return cachePrepStmts;
	}
	public void setCachePrepStmts(String cachePrepStmts) {
		this.cachePrepStmts = cachePrepStmts;
	}
	public Integer getPrepStmtsSize() {
		return prepStmtsSize;
	}
	public void setPrepStmtsSize(Integer prepStmtsSize) {
		this.prepStmtsSize = prepStmtsSize;
	}
	public Integer getPrepStmtsCacheSqlLimit() {
		return prepStmtsCacheSqlLimit;
	}
	public void setPrepStmtsCacheSqlLimit(Integer prepStmtsCacheSqlLimit) {
		this.prepStmtsCacheSqlLimit = prepStmtsCacheSqlLimit;
	}
	public Boolean getUserServerPrepStmts() {
		return userServerPrepStmts;
	}
	public void setUserServerPrepStmts(Boolean userServerPrepStmts) {
		this.userServerPrepStmts = userServerPrepStmts;
	}
}
