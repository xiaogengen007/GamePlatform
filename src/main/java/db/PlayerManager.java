package db;

import java.sql.*;

public class PlayerManager {
	public static void recordPlayer(String plyName, String plyKey) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:gameplatform.db");
			c.setAutoCommit(false);
			//System.out.println("Open database successfully");
			
			stmt = c.createStatement();
			String sql = "INSERT INTO player(p_id, p_name, p_key, p_point) " +
						 "VALUES (null, '" + plyName + "', '" + plyKey + "', null);";
			stmt.executeUpdate(sql);
			
			stmt.close();
			c.commit();
			c.close();
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ":" + e.getMessage());
			System.exit(0);
		}
		//System.out.println("Records create successfully");
	}
	
	public static boolean checkLogin(String plyName, String inputKey) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:gameplatform.db");
			c.setAutoCommit(false);
			//System.out.println("Open database successfully");
			
			stmt = c.createStatement();
			String sql = "SELECT p_key FROM player" + 
						 "WHERE p_name = '" + plyName + "';";
			ResultSet rs = stmt.executeQuery(sql);
		}
	}


}
