package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import net.sf.json.JSONObject;

import websocket.WebSocket;
import player.Player;

public class GameState {
	public static ArrayList<GameState> games = new ArrayList<GameState>(); //建立一个静态的存储游戏状态的数组
	ArrayList<Player> players = new ArrayList<Player>(); //存储该局游戏的用户信息
	boolean isStarted; //记录该局游戏是否已经开始
	int gameNum = 3; //游戏中的玩家数
	int finshedNum = 0; //完成本轮操作的人数
	int gameType; //游戏类型（1为扫雷）
	int maxTurnTime; //单轮游戏所允许的最长时间
	Integer leftTime; //本轮游戏还剩余的游戏时间
	
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
	public void addPlayer(Player ply) { //将该玩家加入该局游戏中
		if (players.size() < 3) {
			players.add(ply);
		} else {
			System.out.println("distribution error!");
		}
		if (players.size() == 3) {
			this.isStarted = true;
			this.leftTime = this.maxTurnTime; //游戏开始时设置剩余时间为最大时间
		}
		sendForGameState();
		
	}
	public boolean deletePlayer(Player ply) { //将该玩家退出该局游戏
		if (!this.isStarted) { //游戏还未开始时可退出游戏
			players.remove(ply);
			this.sendForGameState();
			return true;
		} else {
			return false;
		}
	}
	public static void distributeRoom(Player ply) { //为玩家分配房间
		if (ply.nowGame != null) return; //之前已经在一个房间里，则不用分配
		GameState flow = null;
		for (int i = 0; i < games.size(); i++) {
			flow = games.get(i);
			if (flow.canAddPlayer()) {
				ply.nowGame = flow;
				flow.addPlayer(ply);
				return;
			}
		}
		flow = new DeminGame();
		flow.addPlayer(ply);
		ply.nowGame = flow;
	}

	public static Player SearchFromName(String username) { //通过用户名搜索用户是否在游戏列表中
		for (GameState gs: games) {
			for (Player ply: gs.players) {
				if (ply.username.equals(username)) {
					return ply; //找到了该玩家
				} else {
					//System.out.println(ply.username + " not " + username);
				}
			}
		}
		System.out.println("can not find this player!");
		return null; //否则返回空
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
		for (Player item : players) {
			try {
				if (item.myWebsocket != null) {
					item.myWebsocket.session.getBasicRemote().sendText(messages);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void refreshTime() { //更新游戏时间
		synchronized (this.leftTime) { //对时间加上互斥锁
			if (this.leftTime > 0) {
				this.leftTime--; //还有时间时减一秒
			} else {
				//否则强制结束这一轮
			}
		}
	}
	
	public void HandleDemin(int clickX, int clickY, int clickType, WebSocket ws) {} //完成扫雷游戏中的用户响应	
	public void revisiting(Player ply) {}; //处理用户重新进入游戏
}
