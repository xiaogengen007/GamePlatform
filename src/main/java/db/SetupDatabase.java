package db;

import java.sql.*;

public class SetupDatabase {
	public static boolean hasSet = false; //记录数据库是否已经建立
	public static boolean Setup() {
		hasSet = true; //调用Setup方法即意味着数据库已经建立
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			//创建Gameplatform.db
			c = DriverManager.getConnection("jdbc:sqlite:C:/resource/database/Gameplatform.db");
			c.setAutoCommit(false);
			//System.out.println("Open database successfully");
			
			stmt = c.createStatement();
			//创建player表
			String sql = "CREATE TABLE player ("
					+ "p_id	INTEGER 	PRIMARY KEY AUTOINCREMENT,"
					+ "p_name			VARCHAR(15)	NOT NULL,"
					+ "p_key			CHAR(64)	NOT NULL,"
					+ "p_salt			CHAR(10)	NOT NULL,"
					+ "p_point			INTEGER		NULL);";
			stmt.executeUpdate(sql);
			//创建game process表
			sql = "CREATE TABLE gameprocess ("
				+ "gp_id INTEGER	PRIMARY KEY 	AUTOINCREMENT,"
				+ "gp_type			INTEGER			NOT NULL,"
				+ "gp_begin_t		DATETIME		NULL"
				+ "gp_end_t			DATETIME		NULL);";
			stmt.executeUpdate(sql);
			//创建part表
			sql = "CREATE TABLE part ("
				+ "pa_id INTEGER	PRIMARY KEY		AUTOINCREMENT,"
				+ "p_id				INTEGER			NOT NULL,"
				+ "gp_id			INTEGER			NOT NULL,"
				+ "pa_res			INTEGER			NULL);";
			stmt.executeUpdate(sql);
			//创建friend表
			sql = "CREATE TABLE friend ("
				+ "f_id INTEGER		PRIMARY KEY		AUTOINCREMENT,"
				+ "p_id1			INTEGER			NOT NULL,"
				+ "p_id2			INTEGER			NOT NULL);";
			stmt.executeUpdate(sql);
			//System.out.println("Create tables successfully");
			stmt.close();
			c.commit();
			c.close();
			System.out.println("succeed set up");
			return true;
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ":" + e.getMessage());
			return false;
		}
	}
	public static void main(String[] args) {
		System.out.println("isOK");
	}
}
