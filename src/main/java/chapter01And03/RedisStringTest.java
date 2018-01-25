package chapter01And03;

import connection.ConnectionPool;
import redis.clients.jedis.Jedis;

/**
 * redis字符串结构
 * @author tracy
 *
 */
public class RedisStringTest {
	//连接池
	private ConnectionPool connectionPool = ConnectionPool.getInstance();
	
	
	public static void main(String[] args) {
		//获取连接
		RedisStringTest stringTest = new RedisStringTest();
		Jedis jedis = stringTest.connectionPool.getConnection();
		//清空当前选择库中的所有数据
		jedis.flushDB();
		
		
		
		
		
		//释放连接
		jedis.close();

	}

}
