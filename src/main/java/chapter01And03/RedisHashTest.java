package chapter01And03;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import connection.ConnectionPool;
import redis.clients.jedis.Jedis;

/**
 * 散列操作
 * @author tracy
 *
 */
public class RedisHashTest {
	
	//连接池
	private ConnectionPool connectionPool = ConnectionPool.getInstance();
	
	/**
	 * 新增
	 */
	public void add(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		//新增键值对,如果字段是哈希表中的一个新建字段，并且值设置成功，返回 1 。 
		//如果哈希表中域字段已经存则返回 0 
		System.out.println(jedis.hset("hash-key", "sub-key1", "value1"));
		System.out.println(jedis.hset("hash-key", "sub-key2", "value2"));
		System.out.println(jedis.hset("hash-key", "sub-key1", "value"));
		System.out.println(jedis.hset("hash-key", "sub-key1", "value3"));
		//释放连接
		jedis.close();
	}
	
	/**
	 * 查询
	 */
	public void get(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		//获取散列包含的所有键值对
		Map<String, String> map = jedis.hgetAll("hash-key");
		for (String key : map.keySet()){
			System.out.println(map.get(key));
		}
		//获取指定散列键的值
		System.out.println(jedis.hget("hash-key", "sub-key1"));
		//获取散列包含的键值的数量
		System.out.println(jedis.hlen("hash-key"));
		//检查给定键是否存在于散列中
		System.out.println(jedis.hexists("hash-key", "sub-key1"));
		//获取散列包含的所有键
		Set<String> keySet = jedis.hkeys("hash-key");
		for (String key : keySet){
			System.out.println(key);
		}
		//获取散列包含的所有值
		List<String> vals = jedis.hvals("hash-key");
		for (String val : vals){
			System.out.println(val);
		}
		//释放连接
		jedis.close();
	}
	
	/**
	 * 删除
	 */
	public void del(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		System.out.println(jedis.hgetAll("hash-key"));
		
		//删除给定的键,成功返回1，否则返回0，其实可以将返回看作是删除的个数
		System.out.println(jedis.hdel("hash-key", "sub-key2"));
		System.out.println(jedis.hgetAll("hash-key"));
		
		//释放连接
		jedis.close();
	}
	
	/**
	 * 修改
	 */
	public void update(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		System.out.println(jedis.hgetAll("hash-key"));
		
		//修改特定键的值
		System.out.println(jedis.hset("hash-key", "sub-key1", "value1"));
		System.out.println(jedis.hgetAll("hash-key"));
		//键的值自增,和字符串一样，对散列中一个尚未存在的键执行自增操作时，redis会将键的值当作0来处理
		System.out.println(jedis.hincrBy("hash-key", "sub-key2", 2));
		System.out.println(jedis.hgetAll("hash-key"));
		//浮点数的自增
		System.out.println(jedis.hincrByFloat("hash-key", "sub-key2", 0.2));
		System.out.println(jedis.hgetAll("hash-key"));
		
		//释放连接
		jedis.close();
	}
	
	/**
	 * 批量处理
	 */
	public void batch(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		
		//批量增加键值对
		Map<String, String> map = new HashMap<String, String>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		System.out.println(jedis.hmset("batch-hash", map));
		System.out.println(jedis.hgetAll("batch-hash"));
		//从散列里面获取一个或多个键的值
		List<String> values = jedis.hmget("batch-hash", "key2", "key3", "key4");
		for (String value : values){
			System.out.println(value);
		}
		//批量修改键值对
		map.put("key1", "value111");
		map.put("key2", "value222");
		System.out.println(jedis.hmset("batch-hash", map));
		System.out.println(jedis.hgetAll("batch-hash"));
		//根据键批量删除键值对，返回成功删除键值对的数量
		System.out.println(jedis.hdel("batch-hash", "key1", "key2", "key4"));
		System.out.println(jedis.hgetAll("batch-hash"));
		//释放连接
		jedis.close();
	}

	public static void main(String[] args) {
		RedisHashTest test = new RedisHashTest();
		//清空当前选中的数据库
		Jedis jedis = test.connectionPool.getConnection();
		jedis.flushDB();
		jedis.close();
		
		System.out.println("========新增========");
		test.add();
		System.out.println("===================");
		
		System.out.println("========查询========");
		test.get();
		System.out.println("===================");
		
		System.out.println("========删除========");
		test.del();
		System.out.println("===================");
		
		System.out.println("========修改========");
		test.update();
		System.out.println("===================");
		
		System.out.println("========批量处理======");
		test.batch();
		System.out.println("===================");
		
	}

}
