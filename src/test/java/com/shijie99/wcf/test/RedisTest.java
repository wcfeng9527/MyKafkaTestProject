package com.shijie99.wcf.test;

import redis.clients.jedis.Jedis;

public class RedisTest {
	public static void main(String[] args) {
		Jedis redisClient = new Jedis("192.168.149.132",6379);
		
		System.out.println(redisClient.ping());;
		redisClient.set("test", "myteste");
		
		System.out.println(redisClient.get("test"));
		redisClient.close();
	}
}
