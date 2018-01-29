package test;

import db.SetupDatabase;

public class DBTest {
	public static void main(String[] args) {
		boolean hasSet = true;
		if (!hasSet) {
			boolean isOK = SetupDatabase.Setup();
			System.out.println(isOK + " to succeed set up database.");
		}
	}
}
