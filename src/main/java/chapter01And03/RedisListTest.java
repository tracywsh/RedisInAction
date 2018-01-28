package chapter01And03;

import java.util.List;

import connection.ConnectionPool;
import redis.clients.jedis.Jedis;

/**
 * 列表操作
 * @author tracy
 *
 */
public class RedisListTest {
	//连接池
	private ConnectionPool connectionPool = ConnectionPool.getInstance();
	
	/**
	 * 新增
	 */
	public void add(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		//向列表推入新元素之后，会返回当前列表的长度
		//向右端推入元素
		System.out.println(jedis.rpush("list-key", "item"));
		System.out.println(jedis.rpush("list-key", "item2"));
		//向列表左端推入元素
		System.out.println(jedis.lpush("list-key", "item0"));
		//向列表左端推入已存在的元素
		System.out.println(jedis.lpush("list-key", "item"));
		//释放连接
		jedis.close();
	}
	
	/**
	 * 查询
	 */
	public void get(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		//查询列表元素个数
		System.out.println(jedis.llen("list-key"));
		//查询所有元素
		List<String> values = jedis.lrange("list-key", 0, -1);
		for (String value : values){
			System.out.println(value);
		}
		//查询索引为3的元素，从0开始
		System.out.println(jedis.lindex("list-key", 3));
		//返回列表从start偏移量到end偏移量范围内的所有元素，其中偏移量start和end的元素
		//也会包含在被返回的元素之内
		List<String> subValues = jedis.lrange("list-key", 1, 3);
		for (String subValue : subValues){
			System.out.println(subValue);
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
		System.out.println(jedis.lrange("list-key", 0, -1));
		//弹出列表左侧的元素
		System.out.println(jedis.lpop("list-key"));
		//弹出列表右侧的元素
		System.out.println(jedis.rpop("list-key"));
		System.out.println(jedis.lrange("list-key", 0, -1));
		//释放连接
		jedis.close();
	}
	
	/**
	 * 修改
	 */
	public void update(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		System.out.println(jedis.lrange("list-key", 0, -1));
		// 修改列表中指定下标的值 ,索引从0开始
		jedis.lset("list-key", 1, "item1");
		System.out.println(jedis.lrange("list-key", 0, -1));
		//释放连接
		jedis.close();
	}
	
	/**
	 * 批量操作
	 */
	public void batch(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		
		//------------------批量增加
		
		//同时向右端推入多个元素
		System.out.println(jedis.rpush("list-key-batch", 
				"value1","value2","value3","value","value4","value"));
		//同时向左端推入多个元素,推入后的顺序为value,value-1,value-2
		//也就是先推入value-2,再推入value-1,最后推入value
		System.out.println(jedis.lpush("list-key-batch", "value-2",
				"value-1","value"));

		//输出元素个数
		System.out.println(jedis.llen("list-key-batch"));
		//查询所有元素
		System.out.println(jedis.lrange("list-key-batch",0,-1));
		
		//----------------批量删除
		// 根据参数 COUNT 的值，移除列表中与参数 VALUE 相等的元素。
		//COUNT 的值可以是以下几种：
		//count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT 。
		//count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值。
		//count = 0 : 移除表中所有与 VALUE 相等的值。
		jedis.lrem("list-key-batch", 0, "value");
		System.out.println(jedis.lrange("list-key-batch",0,-1));
		//对列表进行修剪，只保留从start偏移量到end偏移量范围内的元素，其中偏移量为
		//start和偏移量为end的元素也会被保留,起始索引为0
        System.out.println(jedis.ltrim("list-key-batch", 1, 3));
		System.out.println(jedis.lrange("list-key-batch",0,-1));
		//释放连接
		jedis.close();
	}
	
	/**
	 * 阻塞操作
	 */
	public void blockOperation(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		//初始化一些数据
		jedis.rpush("list", "item1", "item2");
		jedis.rpush("list2", "item3");
		//从list2中弹出位于最右端的元素，然后将这个元素推入list列表的最左端，并
		//返回这个元素
		System.out.println(jedis.rpoplpush("list2", "list"));
		System.out.println(jedis.lrange("list",0,-1));
		//当列表不包含任何元素时，阻塞弹出操作会在给定的时限内等待可弹出的元素出现，
		//并在实现到达后返回Null
		System.out.println(jedis.brpoplpush("list2", "list", 1));
		System.out.println(jedis.lrange("list",0,-1));
		//blpop命令会从坐到右地检查传入的列表，并对最先遇到的非空列表执行弹出操作,brpop同理
		System.out.println(jedis.brpoplpush("list", "list2", 1));
		System.out.println(jedis.blpop(1,"list","list2"));
		System.out.println(jedis.blpop(1,"list","list2"));
		List<String> list = jedis.blpop(1,"list","list2");
		for (String str : list){
			System.out.println(str);
		}
		//释放连接
		jedis.close();
	}
	

	public static void main(String[] args) {
		RedisListTest test = new RedisListTest();
		//清空当前选中的数据库
		Jedis jedis = test.connectionPool.getConnection();
		jedis.flushDB();
		jedis.close();
		
		System.out.println("======增加=======");
		test.add();
		System.out.println("================");
		
		System.out.println("=======查询=======");
		test.get();
		System.out.println("================");
		
		System.out.println("=======删除=======");
		test.del();
		System.out.println("=================");
		
		System.out.println("========修改=======");
		test.update();
		System.out.println("==================");
		
		System.out.println("=======批量操作========");
		test.batch();
		System.out.println("====================");
		
		System.out.println("=======阻塞操作========");
		test.blockOperation();
		System.out.println("====================");
	}

}
