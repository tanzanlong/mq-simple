package com.baibei.accountservice.util;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

import redis.clients.jedis.JedisPoolConfig;

@Configuration 
@EnableApolloConfig
public class RedisSessionFactory {
	
	@Value("${REDIS.CLUSTER.SERVERS}")
	private String servers;
	
	@Value("${REDIS.CLUSTER.TIMEOUT}")
	private int timeout;
	
	@Value("${REDIS.CLUSTER.MAX_REDIRECTIONS}")
	private int maxRedirections; 
	
	
	private static final int MAX_TOTAL = 100;
	private static final int MAX_IDLE = 40;
	private static final int MIN_IDLE = 20;
	private static final int MAX_WAITE = 30000;
	
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		String[] jedisClusterNodes = servers.split(",");
        RedisClusterConfiguration clusterConfig=new RedisClusterConfiguration(Arrays.asList(jedisClusterNodes));  
        clusterConfig.setMaxRedirects(maxRedirections);  
        
        JedisPoolConfig poolConfig=new JedisPoolConfig();  
        poolConfig.setMaxWaitMillis(MAX_WAITE);  
        poolConfig.setMaxTotal(MAX_TOTAL);  
        poolConfig.setMinIdle(MIN_IDLE);  
        poolConfig.setMaxIdle(MAX_IDLE);  
        return new JedisConnectionFactory(clusterConfig , poolConfig);
    }

	public String getServers() {
		return servers;
	}

	public void setServers(String servers) {
		this.servers = servers;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getMaxRedirections() {
		return maxRedirections;
	}

	public void setMaxRedirections(int maxRedirections) {
		this.maxRedirections = maxRedirections;
	} 
	
}
