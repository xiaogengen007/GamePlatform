package test;

import db.SetupDatabase;
import db.PlayerManager;

public class DBTest {
	public static void main(String[] args) {
		boolean hasSet = true;
		//if (!hasSet) {
			boolean isOK = SetupDatabase.Setup();
			System.out.println(isOK + " to succeed set up database.");
		//}
		System.out.println(PlayerManager.checkLogin("123", "1234"));	
		System.out.println(PlayerManager.recordPlayer("1", "1"));
		System.out.println(PlayerManager.recordPlayer("2", "2"));
		System.out.println(PlayerManager.recordPlayer("3", "3"));
		System.out.println(PlayerManager.checkLogin("123", "1234"));
	}
}
