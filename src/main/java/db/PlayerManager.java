package db;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerManager {
	
	/**
	 * 记录一名新的玩家
	 * 
	 * return 0; 成功添加该用户，返回0
	 * return 1; 错误类型1：该用户名已经存在
	 * return 2; 错误类型2：数据库异常，注册失败
	 * 
	 * @param plyName
	 * @param plyKey
	 * @param plySalt
	 * @return
	 */
	public static int recordPlayer(String plyName, String plyKey, String plySalt) { //记录新的玩家
		Connection c = null; //与数据库的连接
		Statement stmt = null; //SQL语句环境
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:C:/resource/database/Gameplatform.db");
			c.setAutoCommit(false);		
			stmt = c.createStatement();
			String sql_check = "SELECT * FROM player WHERE p_name = '"
							 + plyName + "';"; //在数据库中寻找该用户名
			ResultSet rs = stmt.executeQuery(sql_check); 
			if(rs.next()) { //若存在与该用户名同名的用户
				stmt.close();
				c.commit();
				c.close();
				System.out.println("错误类型1：该用户名已经存在");
				return 1; //错误类型1：该用户名已经存在
			}
			else {
				String sql = "INSERT INTO player(p_id, p_name, p_key, p_salt, p_point) " +
						 "VALUES (null, '" + plyName + "', '" + plyKey + "', '" + plySalt + 
						 "', 0);";
				stmt.executeUpdate(sql);		
				stmt.close();
				c.commit();
				c.close();
				System.out.println("成功添加该用户");
				return 0; //成功添加该用户，返回0
			}	
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ":" + e.getMessage());
			System.out.println("错误类型2：数据库异常，注册失败");
			return 2; //错误类型2：数据库异常，注册失败
		}
	}
	
	/**
	 * 检查用户登录，返回一个字符串数组，string[0]为他的密码，string[1]为他的盐值
	 * 错误则返回null
	 * 
	 * @param plyName
	 * @return
	 */
	public static String[] checkLogin(String plyName) { //检查登录
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:C:/resource/database/Gameplatform.db");
			c.setAutoCommit(false);		
			stmt = c.createStatement();
			String sql = "SELECT * FROM player WHERE p_name = '"
					 + plyName + "';"; //在数据库中寻找该用户名
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) { //存在该用户
				String[] rtKey = new String[2];
				rtKey[0] = rs.getString("p_key");
				rtKey[1] = rs.getString("p_salt");
				
				stmt.close();
				c.commit();
				c.close();
				return rtKey;
			}
			else { //用户不存在
				stmt.close();
				c.commit();
				c.close();
				System.out.println("该用户不存在（未注册）");
				return null; //该用户不存在（未注册）
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ":" + e.getMessage());
			System.out.println("数据库异常，登录失败");
			return null;//数据库异常，登录失败
		}
	}
	
	/**
	 * 获取用户积分
	 * 
	 * @param plyName
	 * @return
	 */
	public static int getPoint(String plyName) { //获取用户积分
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:C:/resource/database/Gameplatform.db");
			c.setAutoCommit(false);
			
			stmt = c.createStatement();
			String sql = "SELECT * FROM player WHERE p_name = '"
					+ plyName + "';"; //在数据库中寻找该用户名
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) { //该用户存在
				int rtpoint = rs.getInt("p_point"); //存储该用户积分值
				stmt.close();
				c.commit();
				c.close();
				return rtpoint; //返回该用户积分值
			}
			else { //该用户不存在
				stmt.close();
				c.commit();
				c.close();
				return Integer.MIN_VALUE; //用户不存在
			}
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ":" + e.getMessage());
			return Integer.MIN_VALUE; //查询积分异常
		}
	}
	
	/**
	 * 修改用户积分，将修改成功与否结果返回
	 * 
	 * @param plyName
	 * @param deltaPoint
	 * @return
	 */
	public static boolean modifyPoint(String plyName, int deltaPoint) { //修改用户积分
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:C:/resource/database/Gameplatform.db");
			c.setAutoCommit(false);
			
			stmt = c.createStatement();
			int prePoint = PlayerManager.getPoint(plyName);// 获得原分数
			if(prePoint == Integer.MIN_VALUE) {
				return false; //获取分数异常
			}
			String newPoint = String.valueOf(prePoint + deltaPoint); //得到现在的分数(字符形式)
			
			String sql = "UPDATE player SET p_point = " + newPoint + " WHERE p_name = '"
					+ plyName + "';";
			stmt.executeUpdate(sql); //更新用户的积分
			
			stmt.close();
			c.commit();
			c.close();
			return true; //更新积分成功
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ":" + e.getMessage());
			return false; //修改用户积分异常
		}
	}
	
	/**
	 * 根据username获取用户的id
	 * 
	 * @param plyName
	 * @return
	 */
	public static int getId(String plyName) {//获取用户id
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:C:/resource/database/Gameplatform.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();
			
			String sql = "SELECT * FROM player WHERE p_name = '"
					+ plyName + "';";
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) { //该用户存在
				int rtId = rs.getInt("p_id");
				stmt.close();
				c.commit();
				c.close();
				return rtId; //返回该用户id
			}
			else { //该用户不存在
				stmt.close();
				c.commit();
				c.close();
				return -1; //该用户不存在
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ":" + e.getMessage());
			return Integer.MIN_VALUE; //获取用户id异常
		}
	}
	
	/**
	 * 对全体玩家按照积分排序
	 * 输出一个排完序的姓名-积分对应哈希表
	 * 
	 * @return
	 */
	public static Map<String, Integer> sortPoint() { //对全体玩家排序
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:C:/resource/database/Gameplatform.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();
			
			Map<String, Integer> map = new LinkedHashMap<String, Integer>();
			String sql = "SELECT * FROM player ORDER BY p_point DESC"; //按照积分降序排序
			ResultSet rs = stmt.executeQuery(sql);
			//int k = 0;
			while(rs.next()) { //将昵称和积分存储到map中
				map.put(rs.getString("p_name"), rs.getInt("p_point"));
				//k++;
			}
			stmt.close();
			c.commit();
			c.close();
			return map; //返回修改的map
			
		} catch (Exception e) {
			Map<String, Integer> map = new LinkedHashMap<String, Integer>();
			System.err.println(e.getClass().getName() + ":" + e.getMessage());
			return map; //获取失败，返回空map
		}
		/*下面给出map的输出方式
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		map = testPlayerManager.sortPoint();
		if(!map.isEmpty()) { //必须先判断是否为空
			for(Map.Entry<String, Integer> entry : map.entrySet()){  
			    System.out.println("Name = " + entry.getKey() + ", point = " + entry.getValue());  
			}  
		}*/
	}
}
