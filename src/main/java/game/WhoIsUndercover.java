package game;

import java.util.Random;

import net.sf.json.JSONObject;
import player.Player;
import websocket.WebSocket;

public class WhoIsUndercover extends GameState{
	String friendString = ""; //友方的词汇
	String undercoverString = ""; //卧底的词汇
	boolean[] isFriend; //是否为友方，随机生成一个卧底
	boolean[] isAlive; //该用户是否还存活着
	String[] gameMsg; //游戏中大家的消息
	boolean[] finishTurn; //该轮游戏已完成发言
	WhoIsUndercover() {
		super();
		this.gameType = 2; //2表示谁是卧底
		this.gameNum = 4; //设置玩家数为4人
		this.maxTurnTime = 30;
		this.leftTime = new Integer(this.maxTurnTime);
		this.friendString = "大佬";
		this.undercoverString = "大神";
		this.gameMsg = new String [this.gameNum];
		this.finishTurn = new boolean [this.gameNum];
		this.isAlive = new boolean [this.gameNum];
		this.isFriend = new boolean [this.gameNum];
		this.initGame(); //初始化游戏
	}
	
	/*
	 * 初始化游戏
	 */
	public void initGame() { 
		for (int i = 0; i < this.gameNum; i++) {
			this.isAlive[i] = true; //统一设置为都活着
		}
		Random random = new Random();
		int index = Math.abs(random.nextInt()) % this.gameNum;
		for (int i = 0; i < this.gameNum; i++) {
			if (i == index) {
				this.isFriend[i] = false;
			} else {
				this.isFriend[i] = true;
			}
		}
		this.initFinishTurn();
	}
	
	public void initFinishTurn() { //初始化为都未完成该轮游戏
		for (int i = 0; i < this.gameNum; i++) {
				this.finishTurn[i] = false;
		}
	}
	
	public void initPlayers() { //初始化玩家的操作
		for (Player item: players) {
			item.ucPlayer.setPlayer();
		}
	}
	
	public void handleUndercover(String message, WebSocket ws) { //完成谁是卧底中的游戏响应
		
	}
		
	public void sendForMyGameState(JSONObject json) {
		json.put("maxTime", this.maxTurnTime); //单轮最长时间
	}
}
