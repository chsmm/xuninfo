package com.xuninfo.zh.config;

import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
public class MybatisConfiguration {
	
	
	
	@Bean(destroyMethod = "close", initMethod="init")
	public DataSource dataSource(JDBCConfig jdbcConfig){
		DruidDataSource datasource = new DruidDataSource();   
        datasource.setUrl(jdbcConfig.getUrl());  
        datasource.setDriverClassName(new String(Base64.decodeBase64(jdbcConfig.getUrl()))); 
        datasource.setUsername(new String(Base64.decodeBase64(jdbcConfig.getUsername())));  
        datasource.setPassword(new String(Base64.decodeBase64(jdbcConfig.getPassword())));  
        return datasource;
	}
	
	@Bean
	public DataSourceTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource,MybatisConfig mybatisConfig) {
		try {
			SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
			sessionFactory.setDataSource(dataSource);
			sessionFactory.setTypeAliasesPackage(mybatisConfig.getTypeAliasesPackage());
			sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mybatisConfig.getMapperLocations()));
			sessionFactory.setConfigLocation(new DefaultResourceLoader().getResource(mybatisConfig.getConfigLocation()));
			return sessionFactory.getObject();
		} catch (Exception e) {
			return null;
		}
	}

}
