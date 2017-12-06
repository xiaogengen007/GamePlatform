package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import player.Player;
import websocket.WebSocket;

public class WhoIsUndercover extends GameState{
	String friendString = ""; //友方的词汇
	String undercoverString = ""; //卧底的词汇
	int gameProcess; //游戏当前的进程，0表示发言环节，1表示投票环节
	public int maxVotingTime;
	
	/*
	 * 构造函数，完成一些基本的参数配置
	 */
	WhoIsUndercover() {
		super();
		this.gameType = 2; //2表示谁是卧底
		this.gameNum = 4; //设置玩家数为4人
		this.maxTurnTime = 30;
		this.maxVotingTime = 15; //设置投票环节最长时间为15秒
		this.leftTime = new Integer(this.maxTurnTime);
		this.friendString = "大佬";
		this.undercoverString = "大神";
		this.gameProcess = 0; //初始化为发言阶段
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
		for (int i = 0; i < this.players.size(); i++) {
			players.get(i).ucPlayer.isSubmit = false;
		}
	}
	
	public void initPlayers() { //初始化玩家的操作
		for (Player item: players) {
			item.ucPlayer.setPlayer();
		}
	}
	
	/*
	 * 在投票之前初始化，让大家都还没投票
	 */
	public void initBeforeVoting() {
		for (Player item: players) {
			item.ucPlayer.hasVoted = false;
			if (item.ucPlayer.isAlive) {
				item.ucPlayer.canbeVoted = true;
			} else {
				item.ucPlayer.canbeVoted = false;
			}
		}
	}
	
	/*
	 * 获取当前存活的玩家数
	 */
	public int getAliveNum() {
		int countAlive = 0;
		for (Player item: players) {
			if (item.ucPlayer.isAlive) {
				countAlive++;
			}
		}
		return countAlive;
	}
	
	/*
	 * 获取当前已经投票的玩家数（对投票阶段适用）
	 */
	public int getVotedNum() {
		int countVoted = 0;
		for (Player item: players) {
			if (item.ucPlayer.hasVoted) {
				countVoted++;
			}
		}
		return countVoted;
	}
	
	/*
	 * 判断当前投票是否已经完成
	 */
	public boolean finishVote() {
		boolean hasVoted = true;
		for (Player item: players) {
			if (item.ucPlayer.isAlive && !item.ucPlayer.hasVoted) {
				hasVoted = false;
			}
		}
		return hasVoted;
	}
	
	/*
	 * 获取本轮完成发送消息的玩家数
	 */
	public int getSubmitNum() {
		int countAlive = 0;
		for (Player item: players) {
			if (item.ucPlayer.isAlive && item.ucPlayer.isSubmit) {
				countAlive++;
			}
		}
		return countAlive;
	}
	
	/*
	 * 判断大家是否都完成该轮发言
	 */
	public boolean finishThisTurn() {
		boolean hasFinished = true;
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).ucPlayer.isAlive 
					&& !players.get(i).ucPlayer.isSubmit) {
				hasFinished = false;
				break;
			}
		}
		return hasFinished;
	}
	
	/*
	 * 完成谁是卧底中的游戏响应,在非投票阶段
	 * (non-Javadoc)
	 * @see game.GameState#handleUndercover(java.lang.String, websocket.WebSocket)
	 */
	public void handleUndercover(String message, WebSocket ws) { 
		if (ws.myPlayer != null && !ws.myPlayer.ucPlayer.isSubmit
				&& ws.myPlayer.ucPlayer.isAlive) { //判断该玩家是否有"说话"的权力
			ws.myPlayer.ucPlayer.thisTurnMsg = message;
			ws.myPlayer.ucPlayer.isSubmit = true;
			this.sendForGameProcess();
			if (finishThisTurn()) { //该轮结束时进入投票模式
				this.batchHandleTurn();
				this.sendEndOfThisTurn();
			}
		}
	}
	
	/*
	 * 完成投票阶段谁是卧底中的游戏响应
	 * (non-Javadoc)
	 * @see game.GameState#handleUndercoverVoting(java.lang.String, websocket.WebSocket)
	 */
	public void handleUndercoverVoting(int userindex, WebSocket ws) {
		if (ws.myPlayer != null && !ws.myPlayer.ucPlayer.hasVoted 
				&& ws.myPlayer.ucPlayer.isAlive) { //有投票资格且还未投票时才处理
			if (userindex < this.players.size() && 
					this.players.get(userindex).ucPlayer.canbeVoted) {
				ws.myPlayer.ucPlayer.votedPlayer = userindex;
				ws.myPlayer.ucPlayer.hasVoted = true;
				System.out.println(ws.myPlayer.username+" succeed voted.");
				this.sendForVotingProcess();
				if (this.finishVote()) {
					this.batchHandleVoteTurn();
				}
			}
		}
	} 
	
	/*
	 * 当前轮结束时进行批处理(从发言阶段->投票阶段)
	 */
	public void batchHandleTurn() { 
		this.gameProcess = 1; //1表示进入投票环节
		this.leftTime = this.maxVotingTime;
		this.initBeforeVoting(); //在投票前完成相关初始化工作
	}
	
	/*
	 * 当前投票轮结束时进行批处理(从投票阶段->发言阶段（或投票阶段）)
	 */
	public void batchHandleVoteTurn() { 
		this.leftTime = this.maxVotingTime;
		int[] countVoted = new int [this.gameNum];
		for (int i = 0; i < this.gameNum; i++) {
			countVoted[i] = 0;
		}
		for (Player item: players) {
			if (item.ucPlayer.canbeVoted) {
				int votedIndex = item.ucPlayer.votedPlayer;
				countVoted[votedIndex]++;
			}
		}
		ArrayList<Integer> votedMax = new ArrayList<Integer>();
		int maxCount = 0;
		for (int i = 0; i < this.gameNum; i++) {
			if (countVoted[i] == maxCount) {
				votedMax.add(i);
			}
			if (countVoted[i] > maxCount) {
				maxCount = countVoted[i];
				votedMax.clear();
				votedMax.add(i);
			}
		}
		if (votedMax.size() == 1) {
			int maxIndex = votedMax.get(0);
			this.sendEndOfVoteForNextTurn(countVoted, maxIndex);
			this.players.get(maxIndex).ucPlayer.isAlive = false; //设置该人已经死亡
			this.players.get(maxIndex).ucPlayer.canbeVoted = false;
			//这里需要判断游戏是否已经结束
		} else {
			
		}
	}
	
	/*
	 * 该轮游戏发言阶段结束之后向玩家们发送消息
	 */
	public void sendEndOfVoteForNextTurn(int[] countVoted, int maxIndex) {
		JSONObject json1 = new JSONObject();
		json1.put("action", 4); //4表示该轮游戏（投票）结束时发送的消息
		JSONArray jsar1 = new JSONArray();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).ucPlayer.canbeVoted) {
				JSONObject json2 = new JSONObject();
				json2.put("votedName", players.get(i).username);
				json2.put("votedNum", countVoted[i]);
				jsar1.add(json2);
			}
		}
		json1.put("voteResult", jsar1);
		json1.put("diePlayer", players.get(maxIndex).username);
		json1.put("resultType", 1); //在分出胜负时type为1
		String msg = json1.toString();
		System.out.println("send voting message:"+msg);
		for (Player item: players) {
			if (item.myWebsocket != null) {
				try {
					item.myWebsocket.session.getBasicRemote().sendText(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * 该轮游戏发言阶段结束之后向玩家们发送消息
	 */
	public void sendEndOfVoteForNextVoting(int[] countVoted, ArrayList<Integer> votedMax) {
		JSONObject json1 = new JSONObject();
		json1.put("action", 4); //4表示该轮游戏（投票）结束时发送的消息
		JSONArray jsar1 = new JSONArray();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).ucPlayer.canbeVoted) {
				JSONObject json2 = new JSONObject();
				json2.put("votedName", players.get(i).username);
				json2.put("votedNum", countVoted[i]);
				jsar1.add(json2);
			}
		}
		JSONArray jsar2 = new JSONArray();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).ucPlayer.canbeVoted) {
				JSONObject json2 = new JSONObject();
				json2.put("nextVotedIndex", i);
				jsar2.add(json2);
			}
		}
		json1.put("voteResult", jsar1);
		json1.put("nextVoted", jsar2);
		json1.put("resultType", 2); //在未分出胜负时type为2
		String msg = json1.toString();
		System.out.println("send voting message:"+msg);
		for (Player item: players) {
			if (item.myWebsocket != null) {
				try {
					item.myWebsocket.session.getBasicRemote().sendText(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * 该轮游戏发言阶段结束之后向玩家们发送消息
	 */
	public void sendEndOfThisTurn() {
		System.out.println("have send after this speech turn for undercover game.");
		JSONObject json1 = new JSONObject();
		JSONArray jsar1 = new JSONArray();
		for (Player item: players) {
			if (item.ucPlayer.isAlive) { //只统计活着的玩家说了什么
				JSONObject json2 = new JSONObject();
				json2.put("username", item.username);
				json2.put("message", item.ucPlayer.thisTurnMsg);
				if (item.ucPlayer.canbeVoted) {
					json2.put("canVoted", 1); //1表示可以被投票
				} else {
					json2.put("canVoted", 0);
				}
				jsar1.add(json2);
			}
		}
		json1.put("action", 8); //该轮发言阶段结束之后的游戏状态消息发送（这表明已经进入了投票阶段）
		json1.put("leftTime", this.leftTime);
		json1.put("playerNum", this.gameNum);
		json1.put("aliveNum", this.getAliveNum()); 
		JSONArray jsar2 = new JSONArray(); //记录各玩家是否还活着
		for (Player item: players) {
			if (item.ucPlayer.isAlive) {
				jsar2.add(1); //0表示还未存活，1表示还存活
			} else {
				jsar2.add(0);
			}
		}
		json1.put("alive", jsar2);
		json1.put("messages", jsar1);
		String message = json1.toString();
		for (Player item: players) {
			if (item.myWebsocket != null) {
				try {
					item.myWebsocket.session.getBasicRemote().sendText(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * 发送在一轮游戏当中的进度
	 */
	public void sendForGameProcess() {
		JSONObject json1 = new JSONObject();
		if (this.gameProcess == 0) {
			System.out.println("has send game process for undercover");
			JSONArray jsar1 = new JSONArray();
			for (Player item: players) {
				if (item.ucPlayer.isAlive && item.ucPlayer.isSubmit) { //只统计活着的玩家说了什么
					JSONObject json2 = new JSONObject();
					json2.put("username", item.username);
					json2.put("message", item.ucPlayer.thisTurnMsg);
					jsar1.add(json2);
				}
			}
			json1.put("action", 3);
			json1.put("leftTime", this.leftTime);
			json1.put("playerNum", this.gameNum);
			json1.put("aliveNum", this.getAliveNum());
			json1.put("submitNum", this.getSubmitNum());
			json1.put("finished", 0); //0表示还未提交，1表示完成提交，2表示已经死亡
			String message0 = json1.toString();
			json1.put("finished", 1);
			json1.put("preMessage", jsar1);
			String message1 = json1.toString();
			json1.put("finished", 2);
			String message2 = json1.toString();
			for (Player item: players) {
				if (item.myWebsocket != null) {
					try {
						if (!item.ucPlayer.isAlive) {
							item.myWebsocket.session.getBasicRemote().sendText(message2);
						} else {
							if (item.ucPlayer.isSubmit) {
								item.myWebsocket.session.getBasicRemote().sendText(message1);
							} else {
								item.myWebsocket.session.getBasicRemote().sendText(message0);
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}	
	}
	
	/*
	 * 发送在一轮游戏当中的投票环节进度
	 */
	public void sendForVotingProcess() {
		JSONObject json1 = new JSONObject();
		JSONArray jsar1 = new JSONArray();
		for (Player item: players) {
			if (item.ucPlayer.canbeVoted && item.ucPlayer.hasVoted) {
				JSONObject json2 = new JSONObject();
				json2.put("votePlayer", item.username);
				json2.put("votedPlayer", item.ucPlayer.votedPlayer);
				jsar1.add(json2);
			}
		}
		json1.put("voteInfo", jsar1);
		json1.put("action", 9); //9表示发送投票进展
		String msg = json1.toString();
		for (Player item: players) {
			if (item.myWebsocket != null) {
				try {
					item.myWebsocket.session.getBasicRemote().sendText(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void sendForMyGameState(JSONObject json) {
		json.put("maxTime", this.maxTurnTime); //单轮最长时间
		json.put("maxVoteTime", this.maxVotingTime); //单轮投票最长时间
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
