package chapter01And03;

import java.util.List;

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
	
	/**
	 * 增加字符串
	 */
	private void add(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		//增加字符串
		jedis.set("key1", "value1");
		jedis.set("key2", "value2");
		jedis.set("key3", "value3");
		//释放连接
		jedis.close();
	}
	
	/**
	 * 获取字符串
	 */
	private void get(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		System.out.println(jedis.get("key1"));
		System.out.println(jedis.get("key2"));
		System.out.println(jedis.get("key3"));
		//释放连接
		jedis.close();
	}
	
	/**
	 * 删除字符串
	 */
	private void del(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		jedis.del("key3");
		if (!jedis.exists("key3")){
			System.out.println("key3已不存在");
		}
		String value3 = jedis.get("key3");
		if (value3 == null){
			System.out.println("成功删除!");
		}
		//释放连接
		jedis.close();
	}
	
	/**
	 * 修改字符串
	 */
	private void update(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		//直接覆盖原来数据
		jedis.set("key1", "value1-update");
		System.out.println(jedis.get("key1"));
		//追加数据
		jedis.append("key2", "-appendString");
		System.out.println(jedis.get("key2"));
		//释放连接
		jedis.close();
	}
	
	/**
	 * 批量处理
	 */
	private void batchOperation(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		//同时增加多个字符串
		String addStr = jedis.mset("key21","value21","key22","value22",
				"key23","value23","key24","value24");
		System.out.println(addStr);
		//同时获取多个字符串
		List<String> valueList = jedis.mget("key21","key22",
				"key23","key24","key25");
		for (String value : valueList){
			System.out.println(value);
		}
		//同时删除多个key
		jedis.del("key21","key22");
		List<String> valueDelList = jedis.mget("key21","key22",
				"key23","key24","key25");
		for (String valueDel : valueDelList){
			System.out.println(valueDel);
		}
		//释放连接
		jedis.close();
	}
	
	/**
	 * 键存储值的自增自减操作
	 */
	private void InDeCr(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		
		//-------自增------------
		
		//获取一个不存在的key，得到null
		System.out.println(jedis.get("cr"));
		//跟set一样，当key不存在时，incr会为key初始化为0，incr后为1
		System.out.println(jedis.incr("cr"));
		//用incrBy来通过可选的参数指定自增操作的数量
		System.out.println(jedis.incrBy("cr", 15));
		//通过incrByFloat来制定自增操作的数量为小数
		System.out.println(jedis.incrByFloat("cr", 1.23));
		
		//------自减(操作不支持小数)------------
		
		//此时汇报异常，因为cr此时不是integer类型的
//		System.out.println(jedis.decr("cr"));
		//自减操作,跟set一样，当key不存在时，decr会为key初始化为0，decr后为-1
		System.out.println(jedis.decr("decr"));
		//可以指定自减的数量
		System.out.println(jedis.decrBy("decr", 15));
		//释放连接
		jedis.close();
	}
	
	/**
	 * 字符串的截取操作
	 */
	public void subStrOperation(){
		//获取连接
		Jedis jedis = connectionPool.getConnection();
		//将字符串'hello'追加到目前并不存在的'new-string-key'键里
		//append命令在执行之后会返回字符串当前的长度
		System.out.println(jedis.append("new-string-key", "hello "));
		System.out.println(jedis.append("new-string-key", "world!"));
		//redis的索引以0为开始，在进行范围访问时，范围的终点(endpoint)默认也包含在这个范围之内
		System.out.println(jedis.substr("new-string-key", 3, 7));
		//对字符串执行范围设置操作,会在成功之后返回字符串当前的总长度
		System.out.println(jedis.setrange("new-string-key", 0, "H"));
		System.out.println(jedis.setrange("new-string-key", 6, "W"));
		System.out.println(jedis.get("new-string-key"));
		//setrange名利既可以用户替换字符串里已有的内容，又可以用于增长字符串
		System.out.println(jedis.setrange("new-string-key", 11, ", how are you?"));
		System.out.println(jedis.get("new-string-key"));
		//将e的个数变为6个
		System.out.println(jedis.setrange("new-string-key", 1, "eeeeee"));
		System.out.println(jedis.get("new-string-key"));
	}
	
	
	public static void main(String[] args) {
		RedisStringTest test = new RedisStringTest();
		//清空当前选中的数据库
		Jedis jedis = test.connectionPool.getConnection();
		jedis.flushDB();
		jedis.close();
		
		
		System.out.println("============新增==============");
		test.add();
		System.out.println("============================");
		
		System.out.println("=============获取=============");
		test.get();
		System.out.println("=============================");
		
		System.out.println("=============删除=============");
		test.del();
		System.out.println("=============================");
		
		System.out.println("=============修改=============");
		test.update();
		System.out.println("=============================");
		
		System.out.println("=============批量处理=============");
		test.batchOperation();
		System.out.println("=============================");
		
		System.out.println("===========自增自减操作================");
		test.InDeCr();
		System.out.println("===================================");
		
		System.out.println("==========字符串的截取操作===========");
		test.subStrOperation();
		System.out.println("=================================");
	}

}
