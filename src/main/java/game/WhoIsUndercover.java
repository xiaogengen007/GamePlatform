package game;

import java.io.IOException;
import java.util.Random;

import net.sf.json.JSONObject;
import player.Player;
import websocket.WebSocket;

public class WhoIsUndercover extends GameState{
	String friendString = ""; //友方的词汇
	String undercoverString = ""; //卧底的词汇
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
		this.initGame(); //初始化游戏
	}
	
	/*
	 * 初始化游戏
	 */
	public void initGame() { 
		Random random = new Random();
		int index = Math.abs(random.nextInt()) % this.gameNum;
		for (int i = 0; i < this.gameNum; i++) {
			Player item = players.get(i);
			if (item != null) {
				if (i == index) {
					item.ucPlayer.isUndercover = true;
				} else {
					item.ucPlayer.isUndercover = false;
				}
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
		if (ws.myPlayer != null && !ws.myPlayer.ucPlayer.isSubmit) {
			
		}
	}
		
	public void sendForMyGameState(JSONObject json) {
		json.put("maxTime", this.maxTurnTime); //单轮最长时间
	}
	
	/*
	 * 在游戏开始时还需发送的其他部分（个性化处理）,这里指每个人的关键词
	 */
	public void sendElseGameState() { 
		JSONObject json1 = new JSONObject();
		json1.put("action", 7); //7为输出游戏中的额外状态传输
		json1.put("keyword", this.friendString);
		String msg = json1.toString();
		for (int i = 0; i < players.size(); i++) {
			if (!this.players.get(i).ucPlayer.isUndercover) {
				if (this.players.get(i).myWebsocket != null) {
					try {
						this.players.get(i).myWebsocket.session.getBasicRemote().sendText(msg);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		json1.put("keyword", this.undercoverString);
		msg = json1.toString();
		for (int i = 0; i < players.size(); i++) {
			if (this.players.get(i).ucPlayer.isUndercover) {
				if (this.players.get(i).myWebsocket != null) {
					try {
						this.players.get(i).myWebsocket.session.getBasicRemote().sendText(msg);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println("has send keywords for undercover game!");
	}
}
