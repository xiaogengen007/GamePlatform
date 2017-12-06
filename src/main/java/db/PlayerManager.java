package db;

import java.sql.*;

public class PlayerManager {
	public static boolean recordPlayer(String plyName, String plyKey) { //记录新的玩家
		Connection c = null; //与数据库的连接
		Statement stmt = null; //SQL语句环境
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Gameplatform.db");
			c.setAutoCommit(false);
			//System.out.println("Open database successfully");
			
			stmt = c.createStatement();
			String sql = "INSERT INTO player(p_id, p_name, p_key, p_point) " +
						 "VALUES (null, '" + plyName + "', '" + plyKey + "', null);";
			stmt.executeUpdate(sql);
			
			stmt.close();
			c.commit();
			c.close();
			return true;
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ":" + e.getMessage());
			return false;
		}
		//System.out.println("Records create successfully");
	}
	/*
	public static boolean checkLogin(String plyName, String inputKey) { //检查登录
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Gameplatform.db");
			c.setAutoCommit(false);
			//System.out.println("Open database successfully");
			
			stmt = c.createStatement();
			String sql = "SELECT p_key FROM player" + 
						 "WHERE p_name = '" + plyName + "';";
			ResultSet rs = stmt.executeQuery(sql);
		}
	}
	*/

}
