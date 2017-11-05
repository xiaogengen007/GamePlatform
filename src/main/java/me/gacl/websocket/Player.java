package me.gacl.websocket;

public class Player {
	String username;
	int status; //0表示不在进行游戏，1表示在玩扫雷
	GameState nowGame; //现在正在进行的比赛
	boolean hasClicked; //表示该轮游戏中是否完成有效点击（针对扫雷）
	int clickX; //该轮中点击位置的横坐标
	int clickY; //该轮中点击位置的纵坐标
	boolean clickType; //true为左键，false为右键
	public Player() {
		username = "";
		status = 0;
		hasClicked = false;
		clickX = -1;
		clickY = -1;
	}
	void setName(String name) {
		this.username = name;
	}
	String getName() {
		return this.username;
	}
}
