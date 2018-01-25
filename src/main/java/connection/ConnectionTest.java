package connection;

import redis.clients.jedis.Jedis;

public class ConnectionTest {
	
	private ConnectionPool connectionPool = ConnectionPool.getInstance();

	public static void main(String[] args) {
		//获取连接
		ConnectionTest test = new ConnectionTest();
		Jedis jedis = test.connectionPool.getConnection();
		System.out.println(jedis.get("name"));
		jedis.close();
	}

}
