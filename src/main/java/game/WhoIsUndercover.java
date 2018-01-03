package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import db.PlayerManager;
import db.SetupDatabase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import player.Player;
import player.Player.UndercoverPlayer;
import websocket.WebSocket;

public class WhoIsUndercover extends GameState{
	String friendString = ""; //友方的词汇
	String undercoverString = ""; //卧底的词汇
	int gameProcess; //游戏当前的进程，0表示发言环节，1表示投票环节
	public int maxVotingTime;
	int[] countVoted;
	ArrayList<Integer> votedMax = new ArrayList<Integer>();
	/*
	 * 构造函数，完成一些基本的参数配置
	 */
	WhoIsUndercover() {
		super();
		this.gameType = 2; //2表示谁是卧底
		this.gameNum = 4; //设置玩家数为4人
		countVoted = new int [this.gameNum];
		this.maxTurnTime = 30;
		this.maxVotingTime = 15; //设置投票环节最长时间为15秒
		this.leftTime = new Integer(this.maxTurnTime);
		String[] words = this.getWords();
		this.friendString = words[0];
		this.undercoverString = words[1];
		this.gameProcess = 0; //初始化为发言阶段
	}
	
	/*
	 * 获取一组词汇，第一个为好友词，第二个为卧底词
	 */
	public String[] getWords() {
		ReadTxt rt = ReadTxt.getInstance();
		String string = rt.getRandomConent();
		String[] splits = string.split(" ");
		System.out.println("create the words:"+splits[0]+" | "+splits[1]);
		return splits;
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
		this.gameProcess = 0; //初始化为发言阶段
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
				item.ucPlayer.canVote = true;
				item.ucPlayer.canbeVoted = true;
			} else {
				item.ucPlayer.canVote = false;
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
			if (item.ucPlayer.hasVoted && item.ucPlayer.isAlive && item.ucPlayer.canVote) {
				countVoted++;
			}
		}
		return countVoted;
	}
	
	/*
	 * 获取当前可以被投票的玩家数（对投票阶段适用）
	 */
	public int getCanBeVotedNum() {
		int countCanBeVoted = 0;
		for (Player item: players) {
			if (item.ucPlayer.isAlive && item.ucPlayer.canbeVoted) {
				countCanBeVoted++;
			}
		}
		return countCanBeVoted;
	}
	
	/*
	 * 判断当前投票是否已经完成
	 */
	public boolean finishVote() {
		boolean hasVoted = true;
		for (Player item: players) {
			if (item.ucPlayer.isAlive && !item.ucPlayer.hasVoted && item.ucPlayer.canVote) {
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
	 * 判断游戏是否已经结束
	 * 0表示游戏还未结束
	 * 1表示游戏结束，且盟军获胜
	 * 2表示游戏结束，且卧底获胜
	 */
	public int gameOver() { 
		/*
		 * 四人局中1人为卧底
		 * 卧底如果亡了，则平民胜
		 * 卧底如果能坚持到只剩下两人，则平民败
		 */
		int flag = 0; //flag来记录游戏状态
		if (this.gameNum == 4) { 
			boolean undercoverDied = true;
			for (Player item: players) {
				if (item.ucPlayer.isUndercover && item.ucPlayer.isAlive) {
					undercoverDied = false;
				}
			}
			if (undercoverDied) {
				flag = 1;
			} else {
				if (this.getAliveNum() == 2) {
					flag = 2;
				} 
			}
		}
		return flag;
	}
	
	/*
	 * 过滤用户的发言，去掉其中关键字
	 */
	private String filterString(String message) {
		String filterMessage = message;
		for (int i = 0; i < this.friendString.length(); i++) {
			char keyChar = this.friendString.charAt(i);
			//System.out.println("split friend char:"+keyChar);
			filterMessage = filterMessage.replace(keyChar, '*');
		}
		for (int i = 0; i < this.undercoverString.length(); i++) {
			char keyChar = this.undercoverString.charAt(i);
			filterMessage = filterMessage.replace(keyChar, '*');
		}
		return filterMessage;
	}
	
	/*
	 * 完成谁是卧底中的游戏响应,在非投票阶段
	 * (non-Javadoc)
	 * @see game.GameState#handleUndercover(java.lang.String, websocket.WebSocket)
	 */
	public void handleUndercover(String message, WebSocket ws) { 
		if (this.gameStatus == 1 && ws.myPlayer != null && !ws.myPlayer.ucPlayer.isSubmit
				&& ws.myPlayer.ucPlayer.isAlive) { //判断该玩家是否有"说话"的权力
			String filterMessage = this.filterString(message);
			//System.out.println("after filtered:"+filterMessage);
			ws.myPlayer.ucPlayer.thisTurnMsg = filterMessage;
			ws.myPlayer.ucPlayer.isSubmit = true;
			this.sendForGameProcess();
			if (finishThisTurn()) { //该轮结束时进入投票模式
				this.batchHandleTurn();
				this.sendEndOfThisTurn();
			}
		} else {
			System.out.print("receive but not handle because ");
			if (this.gameStatus != 1) {
				System.out.println("gameStatus is " +this.gameStatus);
			}
			if (ws.myPlayer == null) {
				System.out.println("myplayer is null");
			}
			if (ws.myPlayer != null && ws.myPlayer.ucPlayer.isSubmit) {
				System.out.println("is submit");
			}
			if (ws.myPlayer != null && !ws.myPlayer.ucPlayer.isAlive) {
				System.out.println("has died!");
			} 	
		}
	}
	
	/*
	 * 完成投票阶段谁是卧底中的游戏响应
	 * (non-Javadoc)
	 * @see game.GameState#handleUndercoverVoting(java.lang.String, websocket.WebSocket)
	 */
	public void handleUndercoverVoting(int userindex, WebSocket ws) {
		if (this.gameStatus == 1 && ws.myPlayer != null && !ws.myPlayer.ucPlayer.hasVoted 
				&& ws.myPlayer.ucPlayer.isAlive && ws.myPlayer.ucPlayer.canVote) { //有投票资格且还未投票时才处理
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
		for (int i = 0; i < this.gameNum; i++) {
			countVoted[i] = 0;
		}
		for (Player item: players) {
			if (item.ucPlayer.canVote) {
				int votedIndex = item.ucPlayer.votedPlayer;
				countVoted[votedIndex]++;
			}
		}
		int maxCount = 0;
		votedMax.clear();
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
			this.sendEndOfVoteForNextTurn();
			this.players.get(maxIndex).ucPlayer.isAlive = false; //设置该人已经死亡
			this.players.get(maxIndex).ucPlayer.canbeVoted = false;
			//这里需要判断游戏是否已经结束
			if (this.gameOver() != 0) { //如果游戏结束则结束操作
				this.gameStatus = 2; //标识游戏结束
				this.setPointChange();
				this.sendAfterGame();
				/*
				 * 同时将该游戏房间玩家清空
				 */
				for (Player item: players) {
					item.nowGame = null;
				}
				players.clear();
				//游戏房间还需要重新初始化，即更换词汇
				this.gameStatus = 0;
				String[] words = this.getWords();
				this.friendString = words[0];
				this.undercoverString = words[1];
				
			} else {
				this.initFinishTurn(); //将大家都置为可以说话
				this.gameProcess = 0; //回到发言阶段
				this.leftTime = this.maxTurnTime;
			}		
		} else {
			/*
			 * 在进入下一轮投票前先做好初始化工作
			 * 设定好可以投票和可以被投票的人选
			 */
			for (Player item: players) {
				item.ucPlayer.canbeVoted = false;
			}
			for (Integer indexNoVote: votedMax) {
				players.get(indexNoVote).ucPlayer.canbeVoted = true; //仅设置平分这些玩家可被投票
			}
			if (votedMax.size() != this.getAliveNum()) { //当还有人不是同票时
				for (Player item: players) {
					if (!item.ucPlayer.canbeVoted && item.ucPlayer.isAlive) {
						item.ucPlayer.canVote = true;
					} else {
						item.ucPlayer.canVote = false;
					}
				}
			} else { //否则，所有活着的人都可以投票
				for (Player item: players) {
					if (item.ucPlayer.isAlive) {
						item.ucPlayer.canVote = true;
					} else {
						item.ucPlayer.canVote = false;
					}
				}
			}
			
			this.sendEndOfVoteForNextVoting();
			for (Player item: players) {
				item.ucPlayer.hasVoted = false; //重置每人都没投票，能投票的人继续投票
			}
		}
	}
	
	/*
	 * 该轮游戏发言阶段结束之后向玩家们发送消息
	 */
	public void sendEndOfVoteForNextTurn() {
		int maxIndex = this.votedMax.get(0);
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
	 * 该轮游戏发言阶段结束之后向某个玩家发送消息
	 */
	public void sendEndOfVoteForNextTurn(Player player) {
		int maxIndex = this.votedMax.get(0);
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
		if (player.myWebsocket != null) {
			try {
				player.myWebsocket.session.getBasicRemote().sendText(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	/*
	 * 该轮游戏发言阶段结束之后向玩家们发送消息
	 */
	public void sendEndOfVoteForNextVoting() {
		System.out.println("has send for next vote turn");
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
		JSONArray jsar3 = new JSONArray();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).ucPlayer.canVote) {
				JSONObject json2 = new JSONObject();
				json2.put("nextVoteIndex", i);
				jsar3.add(json2);
			}
		}
		json1.put("voteResult", jsar1);
		json1.put("nextVoted", jsar2);
		json1.put("nextVote", jsar3);
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
			if (item.ucPlayer.canVote && item.ucPlayer.hasVoted) {
				JSONObject json2 = new JSONObject();
				json2.put("votePlayer", item.username);
				json2.put("votedPlayer", item.ucPlayer.votedPlayer);
				jsar1.add(json2);
			}
		}
		json1.put("voteInfo", jsar1);
		json1.put("leftTime", this.leftTime);
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
	
	protected void sendForMyGameState(JSONObject json) {
		json.put("maxTime", this.maxTurnTime); //单轮最长时间
		json.put("maxVoteTime", this.maxVotingTime); //单轮投票最长时间
		json.put("leftTime", this.leftTime); //当前剩余的时间
	}
	
	/*
	 * 在游戏开始时还需发送的其他部分（个性化处理）,这里指每个人的关键词
	 */
	protected void sendElseGameState() { 
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
	
	/*
	 * 游戏后返回玩家游戏结果(包括结果，积分变化等)
	 * (non-Javadoc)
	 * @see game.GameState#sendAfterGame()
	 */
	public void sendAfterGame() { 
		int sign = this.gameOver();
		JSONObject json1 = new JSONObject();
		json1.put("action", 6); //6表示游戏结束时所发送的消息
		JSONArray jsar1 = new JSONArray(); //存储用户信息和排名
		for (Player item: players) {
			JSONObject json2 = new JSONObject();
			json2.put("username", item.username);
			if (item.ucPlayer.isUndercover) {
				json2.put("keyword", this.undercoverString);
				json2.put("undercover", 1); //1表示是卧底，0则不是
			} else {
				json2.put("keyword", this.friendString);
				json2.put("undercover", 0);
			}
			jsar1.add(json2);
		}
		json1.put("players", jsar1);
		json1.put("result", sign); //1为平民获胜，2为卧底获胜
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
	
	/*
	 * (non-Javadoc)
	 * @see game.GameState#revisiting(player.Player)
	 * 处理有玩家重新进入的情况
	 */
	public void revisiting(Player ply) {
		if (this.gameStatus != 1) return;
		System.out.println("has send for revisiting player!");
		this.sendForGameState();
		JSONObject json1 = new JSONObject();
		json1.put("action", 10); //10表示游戏过程中时有用户重新返回时发送的内容
		json1.put("gameProcess", this.gameProcess); //0为发言阶段，1为投票阶段
		if (this.gameProcess == 0) { //在发言阶段返回
			JSONArray jsar1 = new JSONArray(); //存储基本信息
			for (Player item: players) {
				JSONObject json2 = new JSONObject();
				json2.put("username", item.username);
				if (item.ucPlayer.isAlive) {
					json2.put("alive", 1);
				} else {
					json2.put("alive", 0);
				}
				jsar1.add(json2);
			}
			json1.put("baseInfo", jsar1);
			json1.put("leftTime", this.leftTime);
			String messages = json1.toString();
			try {
				if (ply.myWebsocket != null) {
					ply.myWebsocket.session.getBasicRemote().sendText(messages);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.sendForGameProcess();
		} else { //在投票阶段进入
			JSONArray jsar1 = new JSONArray(); //存储基本信息
			for (Player item: players) {
				JSONObject json2 = new JSONObject();
				json2.put("username", item.username);
				if (item.ucPlayer.isAlive) {
					json2.put("alive", 1);
				} else {
					json2.put("alive", 0);
				}
				json2.put("message", item.ucPlayer.thisTurnMsg);
				jsar1.add(json2);
			}
			json1.put("baseInfo", jsar1);
			json1.put("leftTime", this.leftTime);
			JSONArray jsar2 = new JSONArray();
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).ucPlayer.canbeVoted) {
					JSONObject json2 = new JSONObject();
					json2.put("votedIndex", i);
					jsar2.add(json2);
				}
			}
			JSONArray jsar3 = new JSONArray();
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).ucPlayer.canVote) {
					JSONObject json2 = new JSONObject();
					json2.put("voteIndex", i);
					jsar3.add(json2);
				}
			}
			json1.put("userVoted", jsar2);
			json1.put("userVote", jsar3);
			String messages = json1.toString();
			try {
				if (ply.myWebsocket != null) {
					ply.myWebsocket.session.getBasicRemote().sendText(messages);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.sendForVotingProcess();
		}		
	}
	
	/*
	 * (non-Javadoc)
	 * @see game.GameState#handleLeftTimeZero()
	 * 解决时间为零的情况,发言阶段则什么都不说，投票阶段则随机投一个人
	 */
	public void handleLeftTimeZero() {
		if (this.gameStatus != 1) {
			return; //游戏为处在进行状态时直接跳过
		}
		for (Player item: players) {
			if (this.gameProcess == 0) { //发言阶段
				if (!item.ucPlayer.isSubmit && item.ucPlayer.isAlive) {
					synchronized (item) {
						item.ucPlayer.thisTurnMsg = "";
						item.ucPlayer.isSubmit = true;
					}
				}
			} else { //投票阶段
				if (!item.ucPlayer.hasVoted && item.ucPlayer.canVote) {
					synchronized (item) {
						int canBeVotedNum = this.getCanBeVotedNum();
						Random random = new Random();
						int index = random.nextInt(canBeVotedNum);
						int beVotedIndex = 0;
						int nowIndex = -1; //逢一个加一
						while (true) {
							UndercoverPlayer uctmp = this.players.get(beVotedIndex).ucPlayer;
							if (uctmp.canbeVoted && uctmp.isAlive) {
								nowIndex++;
								if (nowIndex == index) {
									break; //相等时则代表就是他了
								}
							}
							beVotedIndex++;
						}
						item.ucPlayer.votedPlayer = beVotedIndex;
						item.ucPlayer.hasVoted = true;
					}
				}
			}
		}
		if (this.gameProcess == 0) {
			this.sendForGameProcess();
			this.batchHandleTurn();
			this.sendEndOfThisTurn();
		} else {
			this.sendForVotingProcess();
			this.batchHandleVoteTurn();
		}
	}
	
	/*
	 * 设置游戏结束时的分数变化，并传送给前端
	 * (non-Javadoc)
	 * @see game.GameState#setPointChange()
	 */
	public void setPointChange() {
		if (SetupDatabase.hasSet == false) {
			boolean setSucceed = SetupDatabase.Setup();
			if (!setSucceed) {
				System.err.println("fail to set the database!");
				return;
			}
		}
		/*
		 * 每个人的分数依据平均分进行变化，目标是使得所有人总的分数变化之和大于0
		 */
		int gameSign = this.gameOver(); //记录游戏状态
		JSONObject json1 = new JSONObject();
		json1.put("action", 11); // 11表示游戏结束后修改积分信息
		JSONArray jsar1 = new JSONArray(); //存储用户信息和排名
		for (Player item: players) {
			JSONObject json2 = new JSONObject();
			json2.put("username", item.username);
			int deltaPoint = 0; //记录分数变化的量
			if (this.gameNum == 4) {
				if (item.ucPlayer.isUndercover) {
					if (gameSign == 2) { //四人局中卧底赢时加7分，其他人减2分
						deltaPoint = 7;
					} else { //四人局中卧底输时减5分，其他人加2分
						deltaPoint = -5;
					}
				} else {
					if (gameSign == 2) {
						deltaPoint = -2;
					} else {
						deltaPoint = 2;
					}
				}
			}
			PlayerManager.modifyPoint(item.username, deltaPoint);
			json2.put("deltaPoint", deltaPoint);
			int point = PlayerManager.getPoint(item.username);
			json2.put("point", point);
			jsar1.add(json2);
		}
		json1.put("players", jsar1);
		System.out.println("point info:"+jsar1.toString());
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
}
