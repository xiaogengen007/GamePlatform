package player;

import game.GameState;
import websocket.WebSocket;

public class Player {
	public String username;
	public WebSocket myWebsocket = null;
	int status; //0表示不在进行游戏，1表示在玩扫雷
	public GameState nowGame; //现在正在进行的比赛
	public boolean hasClicked; //表示该轮游戏中是否完成有效点击（针对扫雷）
	public int clickX; //该轮中点击位置的横坐标
	public int clickY; //该轮中点击位置的纵坐标
	public boolean clickType; //true为左键，false为右键
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
