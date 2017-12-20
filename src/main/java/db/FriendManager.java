package db;

import java.sql.*;

public class FriendManager {
	public static int recordFriend(String plyName_1, String plyName_2) { //记录新的朋友关系
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:D:/resource/datebase/Gameplatform.db");
			c.setAutoCommit(false);		
			stmt = c.createStatement();
			
			int id_1 = PlayerManager.getId(plyName_1);
			int id_2 = PlayerManager.getId(plyName_2);
			String sql_check = "SELECT * FROM friend WHERE p_id1 = "
					+ String.valueOf(id_1) + " AND p_id2 = " 
					+ String.valueOf(id_2) + ";"; //判断是否已经成为好友
			ResultSet rs = stmt.executeQuery(sql_check);
			if(rs.next()) { //好友关系已存在
				stmt.close();
				c.commit();
				c.close();
				return 1; //错误类型1：好友已经存在
			}
			else {
				//同时记录(a,b)和(b,a)的关系
				String sql_1 = "INSERT INTO friend(f_id, p_id1, p_id2) "
						+ "VALUES (null," + String.valueOf(id_1) + ","
						+ String.valueOf(id_2) + ");";
				stmt.executeUpdate(sql_1);
				String sql_2 = "INSERT INTO friend(f_id, p_id1, p_id2) "
						+ "VALUES (null," + String.valueOf(id_2) + ","
						+ String.valueOf(id_1) + ");";
				stmt.executeUpdate(sql_2);

				stmt.close();
				c.commit();
				c.close();
				return 0; //成功添加好友关系，返回0
			}
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ":" + e.getMessage());
			return 2; //错误类型2：记录好友关系失败
		}
	}
}