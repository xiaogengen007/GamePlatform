package db;

import java.sql.*;

public class PlayerManager {
	public static int recordPlayer(String plyName, String plyKey) { //记录新的玩家
		Connection c = null; //与数据库的连接
		Statement stmt = null; //SQL语句环境
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:D:/resource/datebase/Gameplatform.db");
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
				String sql = "INSERT INTO player(p_id, p_name, p_key, p_point) " +
						 "VALUES (null, '" + plyName + "', '" + plyKey + "', 0);";
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
	
	public static int checkLogin(String plyName, String inputKey) { //检查登录
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:D:/resource/datebase/Gameplatform.db");
			c.setAutoCommit(false);		
			stmt = c.createStatement();
			String sql = "SELECT * FROM player WHERE p_name = '"
					 + plyName + "';"; //在数据库中寻找该用户名
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) {//存在该用户
				if(inputKey.equals(rs.getString("p_key"))) { //输入密码与用户名密码匹配
					stmt.close();
					c.commit();
					c.close();
					System.out.println("登录成功");
					return 0; //登录成功，返回0
				}
				else {
					stmt.close();
					c.commit();
					c.close();
					System.out.println("错误类型1：用户名与密码不匹配");
					return 1; //错误类型1：用户名与密码不匹配
				}
			}
			else {
				stmt.close();
				c.commit();
				c.close();
				System.out.println("错误类型2：该用户不存在（未注册）");
				return 2; //错误类型2：该用户不存在（未注册）
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ":" + e.getMessage());
			System.out.println("错误类型3：数据库异常，登录失败");
			return 3;//错误类型3：数据库异常，登录失败
		}
	}
	
	public static int getPoint(String plyName) { //获取用户积分
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:D:/resource/datebase/Gameplatform.db");
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
	
	public static boolean modifyPoint(String plyName, int deltaPoint) { //修改用户积分
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:D:/resource/datebase/Gameplatform.db");
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
}
