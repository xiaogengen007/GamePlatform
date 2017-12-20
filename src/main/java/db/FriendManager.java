package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

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
	
	public static Map<String, Integer> sortFriendPoint(String plyName) { //获取用户好友的积分排行榜（取前5名）
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:D:/resource/datebase/Gameplatform.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();
			Map<String, Integer> map = new LinkedHashMap<String, Integer>();
			int plyId = PlayerManager.getId(plyName); //获得该用户的id
			
			String sql = "SELECT p_name, p_point FROM player INNER JOIN friend "
					+ "ON player.p_id = friend.p_id2 "
					+ "WHERE p_id1 = " + String.valueOf(plyId)
					+ " ORDER by p_point DESC;"; //按照积分降序排序
			
			ResultSet rs = stmt.executeQuery(sql);
			int k = 0;
			while(rs.next() && k < 5) { //取前5名，将昵称和积分存储到map中
				map.put(rs.getString("p_name"), rs.getInt("p_point"));
				k++;
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
	}
	
	public static ArrayList<String> getFriendList(String plyName) { //获取好友列表
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:D:/resource/datebase/Gameplatform.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ArrayList<String> ls = new ArrayList<String>();
			int plyId = PlayerManager.getId(plyName); //获得该用户的id
			
			String sql = "SELECT p_name FROM player INNER JOIN friend "
					+ "ON player.p_id = friend.p_id2 "
					+ "WHERE p_id1 = " + String.valueOf(plyId)
					+ " ORDER BY p_name;"; //获得该用户所有好友
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				ls.add(rs.getString("p_name"));
			}
			
			stmt.close();
			c.commit();
			c.close();
			return ls; //返回修改的列表
			
		} catch (Exception e) {
			ArrayList<String> ls = new ArrayList<String>();
			System.err.println(e.getClass().getName() + ":" + e.getMessage());
			return ls; //获取失败，返回空list
		}
		/*下面给出ArrayList输出方式
		ArrayList<String> ls = new ArrayList<String>();
		ls = testFriendManager.getFriendList("11");
		for(int i = 0 ; i < ls.size() ; i++) {
			  System.out.println(ls.get(i));
		}*/
	}
}