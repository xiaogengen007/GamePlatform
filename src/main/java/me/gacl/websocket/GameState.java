package me.gacl.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import net.sf.json.JSONObject;

public class GameState {
	public static ArrayList<GameState> games = new ArrayList<GameState>(); //建立一个静态的存储游戏状态的数组
	ArrayList<WebSocket> players = new ArrayList<WebSocket>(); //存储该局游戏的用户信息
	boolean isStarted; //记录该局游戏是否已经开始
	int gameType;
	public GameState() {
		games.add(this); //将该游戏加载入游戏数组中
		isStarted = false;
	}
	public boolean canAddPlayer() { //判断该局游戏中是否可以再加玩家
		if (players.size() < 3) {
			return true;
		} else {
			return false;
		}
	}
	public void addPlayer(WebSocket ws1) { //将该玩家加入该局游戏中
		if (players.size() < 3) {
			players.add(ws1);
		} else {
			System.out.println("distribution error!");
		}
		if (players.size() == 3) {
			this.isStarted = true;
		}
		sendForGameState();
		
	}
	public void deletePlayer(WebSocket ws1) { //将该玩家退出该局游戏
		if (!this.isStarted) { //游戏还未开始时可退出游戏
			players.remove(ws1);
		}
	}
	public static void distributeRoom(WebSocket ws1) { //为玩家分配房间
		if (ws1.myPlayer.nowGame != null) return; //之前已经在一个房间里，则不用分配
		GameState flow = null;
		for (int i = 0; i < games.size(); i++) {
			flow = games.get(i);
			if (flow.canAddPlayer()) {
				ws1.myPlayer.nowGame = flow;
				flow.addPlayer(ws1);
				return;
			}
		}
		flow = new DeminGame();
		flow.addPlayer(ws1);
		ws1.myPlayer.nowGame = flow;
	}

	public void sendForGameState() {
		JSONObject json1 = new JSONObject();
		json1.put("action", 1); //1表示发送游戏是否已经开始
		if (this.isStarted) {
			json1.put("start", 1); //1表示游戏已经开始了
		} else {
			json1.put("start", 0);
		}
		json1.put("playerNum", players.size());
		String messages = json1.toString();
		for (WebSocket item : players) {
			try {
				item.session.getBasicRemote().sendText(messages);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
