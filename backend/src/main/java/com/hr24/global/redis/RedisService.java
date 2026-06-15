package com.hr24.global.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;
	
	public void save(
			String key,
			String value,
			long timeout,
			TimeUnit timeUnit
	) {
		redisTemplate.opsForValue().set(
		        key,
		        value,
		        timeout,
		        timeUnit
		);
	}
	
	public String get(String key) {
	    return redisTemplate.opsForValue().get(key);
	}

	public void delete(String key) {
	    redisTemplate.delete(key);
	}
	
	public void save(String key, String value) {
	    redisTemplate.opsForValue().set(key, value);
	}
}
