package baseOperation;

import java.util.Set;

import chapter01And03.RedisZSetTest;
import connection.ConnectionPool;
import redis.clients.jedis.Jedis;

/**
 * key操作
 * @author tracy
 *
 */
public class KeyOperation {
	
	//连接池
    private ConnectionPool connectionPool = ConnectionPool.getInstance();

	public static void main(String[] args) {
		KeyOperation test = new KeyOperation();
		//清空当前选中的数据库
		Jedis jedis = test.connectionPool.getConnection();
		
        // 清空数据 
        System.out.println("清空库中所有数据："+jedis.flushDB());
        
		//释放连接
		jedis.close();
	}

}
