package connection;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class ConnectionPool {
	//Redis服务器IP
	private static String ADDR = "127.0.0.1";
	//Redis的端口号
	private static int PORT = 6379;
	private static int TIMEOUT = 1000;
	private static JedisPool jedisPool = null;
	
	/**
	 * 单例模式
	 */
	private ConnectionPool(){
		//创建连接池
		JedisPoolConfig config = new JedisPoolConfig();
		//最大连接数 ,默认值为8
        config.setMaxTotal(100);  
        //最大空闲连接 ,默认值也是8
        config.setMaxIdle(5);  
		jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT);
	}
	private static class Lazy{
		public static ConnectionPool instance = new ConnectionPool();
	}
	public static ConnectionPool getInstance(){
		return Lazy.instance;
	}
	
	/**
	 * 获取连接，使用完后记得close
	 * @return
	 */
	public Jedis getConnection(){
		return jedisPool.getResource();
	}
}
