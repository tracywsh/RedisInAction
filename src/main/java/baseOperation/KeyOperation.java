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
		
		// 输出系统中所有的key
		Set<String> keySet = jedis.keys("*");
		for (String key : keySet){
			System.out.println(key);
		}
		
		// 判断key否存在 
        System.out.println(jedis.exists("list-key")); 
        System.out.println(jedis.exists("key999")); 
        
        // 删除某个key,若key不存在，则忽略该命令
		System.out.println(jedis.del("zset-i"));
		System.out.println(jedis.exists("zset-i"));
		
		// 设置 key的过期时间
        System.out.println("设置 key2的过期时间为5秒:"+jedis.expire("key2", 5));
        try{ 
            Thread.sleep(2000); 
        } 
        catch (InterruptedException e){ 
        } 
        
        // 查看某个key的剩余生存时间,单位【秒】.永久生存或者不存在的都返回-1
        System.out.println("查看key2的剩余生存时间："+jedis.ttl("key2"));
        
        // 移除某个key的生存时间
        System.out.println("移除key2的生存时间："+jedis.persist("key2"));
        System.out.println("查看key2的剩余生存时间："+jedis.ttl("key2"));
        
        // 查看key所储存的值的类型
        System.out.println(jedis.type("key2"));
        System.out.println(jedis.type("batch-hash"));
        System.out.println(jedis.type("zset-2"));
        
        //修改键名
        System.out.println(jedis.rename("key2", "key22"));
        System.out.println(jedis.exists("key2"));
        System.out.println(jedis.exists("key22"));
        
        //将当前db的key移动到给定的db当中：jedis.move("foo", 1)
        
        System.out.println("清空数据库"+jedis.flushDB());
        
		//释放连接
		jedis.close();
	}

}
