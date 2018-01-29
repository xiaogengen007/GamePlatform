package game;

import java.io.IOException;
import java.util.ArrayList;
import db.FriendManager;
import db.SetupDatabase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import websocket.WebSocket;
import player.Player;

public class GameState {
	public static ArrayList<GameState> games = new ArrayList<GameState>(); //建立一个静态的存储游戏状态的数组
	protected ArrayList<Player> players = new ArrayList<Player>(); //存储该局游戏的用户信息
	protected int gameStatus; //记录该局游戏的状态（0为未开始，1为游戏中，2为已结束）
	protected int gameNum = 3; //游戏中的玩家数
	protected int finishedNum = 0; //完成本轮操作的人数
	protected int gameType; //游戏类型（1为扫雷,2为谁是卧底）
	protected int maxTurnTime; //单轮游戏所允许的最长时间
	protected Integer leftTime; //本轮游戏还剩余的游戏时间
	
	public GameState() {
		games.add(this); //将该游戏加载入游戏数组中
		this.gameStatus = 0; //初始时游戏还未开始
	}
	
	/**
	 * 判断该局游戏中是否可以再加玩家
	 */
	private boolean canAddPlayer() { 
		if (players.size() < this.gameNum) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 将该玩家ply加入该局游戏中
	 */
	private void addPlayer(Player ply) { 
		if (players.size() < this.gameNum) { //判断是否可以开始游戏
			players.add(ply);
		} else {
			System.out.println("distribution error!");
		}
		if (players.size() == this.gameNum) {
			this.gameStatus = 1; //游戏开始
			this.leftTime = this.maxTurnTime; //游戏开始时设置剩余时间为最大时间
			this.initGame(); //初始化游戏
			this.initPlayers();
		}
		sendForGameState();		
	}
	
	/**
	 * 将该玩家退出该局游戏
	 */
	public boolean deletePlayer(Player ply) { 
		if (this.gameStatus == 0) { //游戏还未开始时可退出游戏
			players.remove(ply);
			this.sendForGameState();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 为玩家分配房间
	 * @param ply
	 * @param gameType
	 */
	public static void distributeRoom(Player ply, int gameType) { //为玩家分配房间
		if (ply == null || ply.nowGame != null) return; //之前已经在一个房间里，则不用分配
		GameState flow = null;
		for (int i = 0; i < games.size(); i++) {
			flow = games.get(i);
			if (flow.canAddPlayer() && flow.gameType == gameType) {
				ply.nowGame = flow;
				flow.addPlayer(ply);
				return;
			}
		}
		if (gameType == 1) {
			flow = new DeminGame();
		} else {
			flow = new WhoIsUndercover();
		}
		flow.addPlayer(ply);
		ply.nowGame = flow;
	}
	
	/**
	 * 通过用户名搜索用户是否在游戏列表中
	 * @param username
	 * @return
	 */
	public static Player searchFromName(String username) { //通过用户名搜索用户是否在游戏列表中
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
	
	/**
	 * 
	 * 将游戏状态发送给玩家（未开始-开始-已结束）
	 */
	public void sendForGameState() {
		JSONObject json1 = new JSONObject();
		json1.put("action", 1); //1表示发送游戏是否已经开始
		json1.put("start", this.gameStatus); //0为未开始，1为开始，2为结束
		JSONArray jsar1 = new JSONArray(); //存储各用户的信息
		for (Player item: players) {
			JSONObject json2 = new JSONObject();
			json2.put("username", item.username);
			json2.put("hashcode", item.hashCode);
			jsar1.add(json2);
		}
		json1.put("players", jsar1);
		this.sendForMyGameState(json1); //发送每种游戏额外的东西
		String messages = json1.toString();
		//System.out.println("send:"+messages);
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
		if (this.gameStatus == 1) {
			this.sendElseGameState();
		}
	}
	
	/**
	 * 更新游戏时间
	 */
	public void refreshTime() { 
		synchronized (this.leftTime) { //对时间加上互斥锁
			if (this.leftTime > 0) {
				this.leftTime--; //还有时间时减一秒
			} else {
				//否则强制结束这一轮
				handleLeftTimeZero();
			}
		}
	}
	
	/**
	 * 解决用户在游戏中聊天的功能
	 */
	public void handleForChating(String message) {
		JSONObject json1 = new JSONObject();
		json1.put("action", 5); //5表示游戏中聊天
		json1.put("message",message); 
		String messages = json1.toString();
		for (Player item: players) {
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
	
	/**
	 * 在房间里加好友
	 */
	public void addFriendInRoom(String username1, String username2) {
		if (!SetupDatabase.hasSet) {
			SetupDatabase.Setup();
		}
		int addState = FriendManager.recordFriend(username1, username2);
		JSONObject json1 = new JSONObject();
		json1.put("username1", username1);
		json1.put("username2", username2);
		json1.put("result", addState); //0表示成功，1表示已经是好友，2表示失败
		String messages = json1.toString();
		for (Player item: this.players) {
			if (item.username.equals(username1) || item.username.equals(username2)) {
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
	}
	
	public void handleDemin(int clickX, int clickY, int clickType, WebSocket ws) {} //完成扫雷游戏中的用户响应	
	public void handleUndercover(String message, WebSocket ws) {} //完成谁是卧底中的游戏响应
	public void handleUndercoverVoting(int userindex, WebSocket ws) {} //完成投票阶段谁是卧底中的游戏响应
	public void revisiting(Player ply) {} //处理用户重新进入游戏
	public void handleLeftTimeZero() {} //解决时间为零的情况
	protected void sendForMyGameState(JSONObject json) {} //发送每个游戏状态特殊的部分
	protected void sendElseGameState() {} //在游戏开始时还需发送的其他部分（个性化处理）
	public void sendAfterGame() {} //游戏后返回玩家游戏结果
	protected void initPlayers() {} //初始化玩家的信息
	protected void initGame() {} //初始化游戏
	public void setPointChange() {} //设置游戏结束时的分数变化，并传送给前端
}
