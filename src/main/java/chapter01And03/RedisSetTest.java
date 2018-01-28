package chapter01And03;

import java.util.List;
import java.util.Set;

import connection.ConnectionPool;
import redis.clients.jedis.Jedis;

/**
 * 集合操作
 * @author tracy
 *
 */
public class RedisSetTest {
	//连接池
	private ConnectionPool connectionPool = ConnectionPool.getInstance();
	
	/**
	 * 增加
	 */
	public void add(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		//向集合中添加元素，返回以代表成功添加，返回0表示这个元素已经存在集合中
		jedis.sadd("set-key", "item1");
		jedis.sadd("set-key", "item2");
		jedis.sadd("set-key", "item3");
		System.out.println(jedis.sadd("set-key", "item1"));
		//释放连接
		jedis.close();
	}
	
	/**
	 * 查询
	 */
	public void get(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		//查询给定元素是否存在于集合中
		System.out.println(jedis.sismember("set-key", "item1"));
		System.out.println(jedis.sismember("set-key", "item"));
		//查询所有元素
		Set<String> set = jedis.smembers("set-key");
		for (String str : set){
			System.out.println(str);
		}
		//查询集合包含的元素数量
		System.out.println(jedis.scard("set-key"));
		//从集合里面随机的返回一个或多个元素
        //如果 count 为正数，且小于集合基数，那么命令返回一个包含 count 个元素的数组，数组中的元素各不相同。如果 count 大于等于集合基数，那么返回整个集合。
        //如果 count 为负数，那么命令返回一个数组，数组中的元素可能会重复出现多次，而数组的长度为 count 的绝对值。
		List<String> randomList = jedis.srandmember("set-key", -10);
		for (String randomValue : randomList){
			System.out.println(randomValue);
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
		//移除给定元素,返回被移除元素的数量
		System.out.println(jedis.srem("set-key", "item1"));
		System.out.println(jedis.smembers("set-key"));
		//随机的移除集合中的一个元素
		System.out.println(jedis.spop("set-key"));
		System.out.println(jedis.smembers("set-key"));
		
		//将set-key中的元素item1添加到new-set-key中，成功返回1，否则为0
		jedis.sadd("set-key", "item1");
		System.out.println(jedis.smove("set-key", "new-set-key", "item1"));
		System.out.println(jedis.smembers("new-set-key"));
		
		//释放连接
		jedis.close();
	}
	
	/**
	 * 批量操作
	 */
	public void batch(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		//批量增加,返回成功添加的数量
		System.out.println(jedis.sadd("batch-set", "value", "value1", 
				"value2", "value3", "value"));
		System.out.println(jedis.smembers("batch-set"));
		
		//批量删除，返回成功删除的数量
		System.out.println(jedis.srem("batch-set", "value", "value1","value4",
				"value5"));
		System.out.println(jedis.smembers("batch-set"));
		//释放连接
		jedis.close();
	}
	
	/**
	 * 集合运算
	 */
	public void operation(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		jedis.sadd("set1", "a", "b", "c", "d");
		jedis.sadd("set2", "c", "d", "e", "f");
		
		//计算差集(第一个集合与其他集合，此处就是set1与其他集合set2的差集)
		Set<String> diffSet = jedis.sdiff("set1","set2");
		for (String value : diffSet){
			System.out.println(value);
		}
		//计算差集并存储,返回存储到set-diff集合的个数
		System.out.println(jedis.sdiffstore("set-diff", "set1","set2"));
		System.out.println(jedis.smembers("set-diff"));
		
		//计算交集
		Set<String> interSet = jedis.sinter("set1","set2");
		for (String value : interSet){
			System.out.println(value);
		}
		//计算交集并存储,返回存储到set-inter集合的个数
		System.out.println(jedis.sinterstore("set-inter", "set1", "set2"));
		System.out.println(jedis.smembers("set-inter"));
		
		//计算并集
		Set<String> unionSet = jedis.sunion("set1", "set2");
		for (String value : unionSet){
			System.out.println(value);
		}
		//计算并集并存储,返回存储到set-union集合的个数
		System.out.println(jedis.sunionstore("set-union", "set1", "set2"));
		System.out.println(jedis.smembers("set-union"));
		//释放连接
		jedis.close();
	}
	
	public static void main(String[] args) {
		RedisSetTest test = new RedisSetTest();
		//清空当前选中的数据库
		Jedis jedis = test.connectionPool.getConnection();
		jedis.flushDB();
		jedis.close();
		
		System.out.println("=========增加========");
		test.add();
		System.out.println("====================");
		
		System.out.println("=========查询========");
		test.get();
		System.out.println("====================");
		
		System.out.println("=========删除========");
		test.del();
		System.out.println("====================");
		
		System.out.println("=======批量操作========");
		test.batch();
		System.out.println("====================");
		
		System.out.println("=======集合运算========");
		test.operation();
		System.out.println("====================");
		
	}
}
