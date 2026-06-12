package com.hr24.global.redis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RedisController {

	private final RedisService redisService;
	
	@GetMapping("/redis/test")
	public String test() {
		
		redisService.save(
				"test",
				"Hello, Redis!"
		);
		
		return redisService.get("test");
	}
}
