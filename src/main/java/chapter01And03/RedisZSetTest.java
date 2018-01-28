package chapter01And03;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import connection.ConnectionPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ZParams;

/**
 * 有序集合
 * @author tracy
 *
 */
public class RedisZSetTest {
	//连接池
	private ConnectionPool connectionPool = ConnectionPool.getInstance();
	
	/**
	 * 新增
	 */
	public void add(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		
		//新增成员和分值，如果有序集合中的成员已存在则返回0，否则返回1
		System.out.println(jedis.zadd("zset-key", 782, "member0"));
		System.out.println(jedis.zadd("zset-key", 982, "member2"));
		System.out.println(jedis.zadd("zset-key", 882, "member1"));
		System.out.println(jedis.zadd("zset-key", 882, "member1"));
		
		//释放连接
		jedis.close();
	}
	
    /**
     * 删除
     */
	public void del(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		System.out.println(jedis.zrange("zset-key", 0, -1));
		
		//删除有序集合中的指定成员，成功返回1，否则返回0
		System.out.println(jedis.zrem("zset-key", "member0"));
		System.out.println(jedis.zrange("zset-key", 0, -1));
		
		//释放连接
		jedis.close();
	}
	
	/**
	 * 修改
	 */
	public void update(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		System.out.println(jedis.zrange("zset-key", 0, -1));
		
		//直接修改成员对应的分值
		System.out.println(jedis.zscore("zset-key", "member1"));
		System.out.println(jedis.zadd("zset-key", 872, "member1"));
		System.out.println(jedis.zscore("zset-key", "member1"));
		//成员分值的自增操作
		System.out.println(jedis.zincrby("zset-key", 0.2, "member1"));
		System.out.println(jedis.zscore("zset-key", "member1"));
		
		//释放连接
		jedis.close();
	}
	
	/**
	 * 批量操作
	 */
	public void batch(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		
		//批量增加,返回成功增加的个数
		Map<String,Double> map = new HashMap<String, Double>();
		map.put("m1", 1.0);
		map.put("m2", 2.0);
		map.put("m3", 3.0);
		map.put("m2", 1.3);
		System.out.println(jedis.zadd("key", map));
		System.out.println(jedis.zrangeWithScores("key", 0, -1));
		
		//批量修改
		map.put("m1", 1.1);
		map.put("m2", 2.2);
		map.put("m3", 3.3);
		System.out.println(jedis.zadd("key", map));
		System.out.println(jedis.zrangeWithScores("key", 0, -1));
		
		//批量删除,删除给定的成员，返回成功移除的成员的个数
		System.out.println(jedis.zrem("key", "m2", "m3", "m4"));
		System.out.println(jedis.zrange("key", 0, -1));
		
		//移除有序集合中排名介于start和stop之间的所有成员,包括start和stop
		//返回移除的个数
		jedis.zadd("key", 2.2, "m2");
		jedis.zadd("key", 3.3, "m3");
		jedis.zadd("key", 4.4, "m4");
		System.out.println(jedis.zremrangeByRank("key", 1, 3));
		System.out.println(jedis.zrange("key", 0, -1));
		
		//移除有序集合中分值介于min和max之间的所有成员,包括min和max
		//返回移除的个数
		jedis.zadd("key", 2.2, "m2");
		jedis.zadd("key", 3.3, "m3");
		jedis.zadd("key", 4.4, "m4");
		jedis.zadd("key", 5.5, "m5");
		System.out.println(jedis.zremrangeByScore("key", 2.2, 4.4));
		System.out.println(jedis.zrange("key", 0, -1));
		
		//释放连接
		jedis.close();
	}
	
	/**
	 * 查询
	 */
	public void get(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		Map<String,Double> map = new HashMap<String, Double>();
		map.put("m1", 1.1);
		map.put("m2", 2.2);
		map.put("m3", 3.3);
		map.put("m4", 4.4);
		map.put("m5", 5.5);
		//5
		jedis.zadd("key1", map);
		//[[[109, 49],1.1], [[109, 50],2.2], [[109, 51],3.3],
		//[[109, 52],4.4], [[109, 53],5.5]]
		jedis.zrangeWithScores("key1", 0, -1);
		
		//查询有序集合包含的成员数量
		//5
		jedis.zcard("key1");
		//查询分值介于min和max之间的成员数量,包括min和max
		//3
		jedis.zcount("key1", 2.2, 4.4);
		//3
		jedis.zcount("key1", 2.0, 5.0);
		//返回成员在有序集合中的排名,从0开始
		//如果不存在该成员则返回null
		//2
		jedis.zrank("key1", "m3");
		//返回有序集合里成员的排名，成员按照分值从大到小排列
		//结果为0
		jedis.zrevrank("key1", "m5");
		//查询成员的分值
		//2.2
		jedis.zscore("key1", "m2");
		//查询有序集合中排名介于1和3之间的成员,从0开始
		Set<String> memSet = jedis.zrange("key1", 1, 3);
		for (String mem : memSet){
			System.out.println(mem);
		}
		//查询有序集合中排名介于1和3之间的成员和分值,从0开始
		//[[[109, 50],2.2], [[109, 51],3.3], [[109, 52],4.4]]
		jedis.zrangeWithScores("key1", 1, 3);
		//返回有序集合给定排名范围内的成员，成员分值从大到小排序,从0开始,包括排名的值
		System.out.println(jedis.zrevrange("key1", 0, 2));
		System.out.println(jedis.zrevrangeWithScores("key1", 0, 2));
		//返回有序集合中，分值介于min和max之间的所有成员,包含min和max
		System.out.println(jedis.zrangeByScore("key1", 2.2, 4.4));
		System.out.println(jedis.zrangeByScoreWithScores("key1", 2.2, 4.4));
		//获取有序集合中分值介于min和max（包括）之间的所有成员，并按照分值从大到小的顺序来返回它们
		System.out.println(jedis.zrevrangeByScore("key1", 4.4, 2.2));
		System.out.println(jedis.zrevrangeByScoreWithScores("key1", 4.4, 2.2));
		
		//释放连接
		jedis.close();
	}
	
	/**
	 * 集合运算
	 */
	public void operation(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		Map<String,Double> map1 = new HashMap<String, Double>();
		map1.put("a", 1.0);
		map1.put("b", 2.0);
		map1.put("c", 3.0);
		jedis.zadd("zset-1", map1);
		Map<String,Double> map2 = new HashMap<String, Double>();
		map2.put("b", 4.0);
		map2.put("c", 1.0);
		map2.put("d", 0.0);
		jedis.zadd("zset-2", map2);
		
		//交集运算,返回交集的个数
		//ZINTERSTORE和ZUNIONSTORE默认使用的聚合函数为sum,这个函数会把各个有序集合的成员
		//的分值都加起来
		System.out.println(jedis.zinterstore("zset-i", "zset-1", "zset-2"));
		System.out.println(jedis.zrangeWithScores("zset-i", 0, -1));
		
		//并集运算,返回并集的个数
		//用户可以在执行并集运算和交集运算的时候传入不同的聚合函数，共有sum、min、max三个聚合函数可选
		ZParams zParams = new ZParams();
	    zParams.aggregate(ZParams.Aggregate.MIN);
		System.out.println(jedis.zunionstore("zset-u", zParams, "zset-1", "zset-2"));
		System.out.println(jedis.zrangeWithScores("zset-u", 0, -1));
		
		//还可以把集合作为输入传给ZINTERSTORE和ZUNIONSTORE
		//命令会将集合看作是成员分值全为1的有序集合来处理
		jedis.sadd("set-1", "a", "d");
		jedis.zunionstore("zset-u2", "zset-1", "zset-2", "set-1");
		System.out.println(jedis.zrangeWithScores("zset-u2", 0, -1));
		
		//释放连接
		jedis.close();
	}

	public static void main(String[] args) {
		RedisZSetTest test = new RedisZSetTest();
		//清空当前选中的数据库
		Jedis jedis = test.connectionPool.getConnection();
		jedis.flushDB();
		jedis.close();
		
		System.out.println("======新增====");
		test.add();
		System.out.println("=============");
		
		System.out.println("======删除====");
		test.del();
		System.out.println("=============");
		
		System.out.println("======修改====");
		test.update();
		System.out.println("=============");
		
		System.out.println("======批量操作====");
		test.batch();
		System.out.println("===============");
		
		System.out.println("======查询====");
		test.get();
		System.out.println("===============");
		
		System.out.println("======集合运算====");
		test.operation();
		System.out.println("================");
	}

}
