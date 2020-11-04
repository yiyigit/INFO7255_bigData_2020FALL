package connection;

import redis.clients.jedis.Jedis;

public class RedisConnection {
	private static Jedis jedis;

	public static Jedis getConnection() {
			Jedis jedis = new Jedis();
			System.out.println("Connected to server sucessfully. Congrats Yiyi");
			return jedis;
	}
}
