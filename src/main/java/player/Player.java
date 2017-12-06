package player;

import game.GameState;
import websocket.WebSocket;

public class Player {
	public String username;
	public WebSocket myWebsocket = null;
	int status; //0表示不在进行游戏，1表示在玩扫雷
	public GameState nowGame; //现在正在进行的比赛
	public DeminPlayer deminPlayer = new DeminPlayer(); //扫雷游戏玩家记录
	public UndercoverPlayer ucPlayer = new UndercoverPlayer(); //谁是卧底玩家记录
	public Player() {
		username = "";
		status = 0;
	}
	void setName(String name) {
		this.username = name;
	}
	String getName() {
		return this.username;
	}
	public class DeminPlayer {
		public boolean hasClicked; //表示该轮游戏中是否完成有效点击（针对扫雷）
		public int clickX; //该轮中点击位置的横坐标
		public int clickY; //该轮中点击位置的纵坐标
		public boolean clickType; //true为左键，false为右键
		public int score; //游戏中的积分
		public DeminPlayer() {
			this.hasClicked = false;
			this.clickX = -1;
			this.clickY = -1;
			this.score = 0; //初始化游戏积分为零
		}
		public void setPlayer() {
			this.score = 0;
			this.hasClicked = false;
		}
	}
	public class UndercoverPlayer {
		public boolean isUndercover; //是否为卧底
		public boolean isSubmit; //这轮是否提交
		public boolean isAlive; //是否已经阵亡
		public String thisTurnMsg; //该轮玩家发言
		UndercoverPlayer() {
			this.isSubmit = false;
			this.isAlive = true;
		}
		public void setPlayer() {
			this.isSubmit = false;
			this.isAlive = true;
		}
	}
}
