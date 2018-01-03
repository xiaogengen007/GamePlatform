package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import db.PlayerManager;
import db.SetupDatabase;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import websocket.WebSocket;
import player.Player;

public class DeminGame extends GameState{
	private static final int gridLen = 8; //雷区的大小
	private static final int scorePerGrid = 6; //每轮单格的加分
	int[][] gridClicked = new int [gridLen+2][gridLen+2]; //0表示还未有人选中，1表示左键，2表示右键
	boolean[][] isMine = new boolean [gridLen+2][gridLen+2]; //每个格子是否为地雷
	int[][] gridState = new int [gridLen+2][gridLen+2];  //0~8为地雷数，9为标红雷，10为误踩雷
	int totalMine; //地雷总数
	int leftMine; //剩余地雷数
	DeminGame() {
		super();
		this.gameType = 1; //扫雷为类型1
		this.maxTurnTime = 8; //扫雷游戏设置的一轮游戏时间为20s
		this.leftTime = new Integer(this.maxTurnTime);
		for (int i = 1; i < gridLen+1; i++) {
			for (int j= 1; j < gridLen+1; j++) {
				gridClicked[i][j] = 0; 
				this.gridState[i][j] = 0;
				this.isMine[i][j] = false; //默认都不是地雷
			}
		}
		this.generatingMine();
	}
	
	public void generatingMine() { //初始化生成游戏中的雷
		int low_bound = (int)(gridLen*gridLen*0.25); //设置雷数的最低值
		int high_bound = (int)(gridLen*gridLen*0.46); //设置雷数的最高值
		int possible = high_bound - low_bound + 1; //设置雷数的区间大小
		Random random = new Random(); //设置随机数种子
		this.leftMine = low_bound + Math.abs(random.nextInt())%possible; //设置雷的数目
		//this.leftMine = 10;
		this.totalMine = this.leftMine;
		for (int i = 0; i < leftMine; i++) {
			int nums = this.gridLen * this.gridLen - i; //所有可能取到的位置数
			int index = Math.abs(random.nextInt()) % nums;
			for (int j = 0; j < this.gridLen*this.gridLen; j++) {
				int x = j / this.gridLen + 1;
				int y = j % this.gridLen + 1;
				if (!this.isMine[x][y]) {
					if (index == 0) {
						this.isMine[x][y] = true;
						break;
					} else {
						index--;
					}
				}
			}		
		}
		for (int i = 1; i < this.gridLen + 1; i++) {
			for (int j = 1; j < this.gridLen + 1; j++) {
				if (this.isMine[i][j]) {
					this.gridState[i][j] = 9; //有雷的话默认为标红雷
				} else {
					//否则计数周围有多少个雷
					int count = 0;
					for (int k = i-1; k <= i+1; k++) {
						for (int l = j-1; l <= j+1; l++) {
							if (k > 0 && k <= this.gridLen && l > 0 && l <= this.gridLen) {
								if (this.isMine[k][l]) {
									count++;
								}							
							}
						}
					}
					this.gridState[i][j] = count;
				}
			}
		}
	}
	
	public void initPlayers() { //初始化玩家的操作
		for (Player item: players) {
			item.deminPlayer.setPlayer();
		}
	}
	
	boolean gameOver() { //判断现在游戏是否已经结束
		if (leftNoneClicked() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	boolean clickRight(Player ply) { //判断用户这次点击是不是正确
		if ((ply.deminPlayer.clickType && !this.isMine[ply.deminPlayer.clickX+1][ply.deminPlayer.clickY+1]) || 
				(!ply.deminPlayer.clickType && this.isMine[ply.deminPlayer.clickX+1][ply.deminPlayer.clickY+1])) {
			return true;
		} else {
			return false;
		}
	}
	
	boolean clickRight(boolean clickType, int clickX, int clickY) { //判断用户这次点击是不是正确（重载）
		if ((clickType && !this.isMine[clickX+1][clickY+1]) || 
				(!clickType && this.isMine[clickX+1][clickY+1])) {
			return true;
		} else {
			return false;
		}
	}
	
	int calculateLeftMine() { //计算剩余的雷数
		int count = 0;
		for (int i = 1; i < this.gridLen+1; i++) {
			for (int j = 1; j < this.gridLen+1; j++) {
				if (this.gridClicked[i][j] != 1 && this.gridClicked[i][j] != 2 && this.isMine[i][j]) {
					count++;
				}
			}
		}
		return count;
	}
	
	int leftNoneClicked() { //在该轮之前还未被点击方格的个数
		int count = 0;
		for (int i = 1; i < this.gridLen+1; i++) {
			for (int j = 1; j < this.gridLen+1; j++) {
				if (this.gridClicked[i][j] != 1 && this.gridClicked[i][j] != 2) {
					count++;
				}
			}
		}
		return count;
	}
	
	public void handleDemin(int clickX, int clickY, int clickType, WebSocket ws) { //完成扫雷游戏中的用户响应
		//System.out.println("leftTime: "+ this.leftTime);
		if (this.gameStatus == 1 && !ws.myPlayer.deminPlayer.hasClicked && this.gridClicked[clickX+1][clickY+1] != 1 && this.gridClicked[clickX+1][clickY+1] != 2) { //该方格还未选过时响应该次选方格
			synchronized (ws.myPlayer) {
				if (ws.myPlayer.deminPlayer.hasClicked) {
					return; //被系统自动处理后即退出
				} else {
					ws.myPlayer.deminPlayer.hasClicked = true; //完成该轮扫雷工作
				}
			}
			ws.myPlayer.deminPlayer.clickX = clickX;
			ws.myPlayer.deminPlayer.clickY = clickY;
			if (clickType == 0) {
				ws.myPlayer.deminPlayer.clickType = true;
			} else {
				ws.myPlayer.deminPlayer.clickType = false;
			}
			this.finishedNum++;
			if (this.finishedNum < this.gameNum) {
				sendForGameProcess(); //该轮未结束时告知用户进展情况
			} else {
				this.sendForGameProcess();
				this.batchHandleTurn(); //完成此轮后进行一些常规操作
			}
		}	
	}
	
	/*
	 * 发送在一轮游戏当中的进度
	 */
	public void sendForGameProcess() {
		JSONObject json1 = new JSONObject();
		json1.put("action", 3); //3表示扫雷该轮仍处于进行状态
		json1.put("finished", 0); //0表示还未完成
		json1.put("playerNum", players.size());
		json1.put("finishNum", this.finishedNum);
		json1.put("leftTime", this.leftTime); //发送本轮剩余时间
		String messages2 = json1.toString(); //传给没有完成click的玩家
		json1.put("finished", 1);
		//发送已经完成的用户click信息
		JSONArray jsar1 = new JSONArray();
		for (Player item: players) {
			if (item.deminPlayer.hasClicked) {
				JSONObject json2 = new JSONObject();
				if (item.deminPlayer.clickType) {
					json2.put("clickType",1);
				} else {
					json2.put("clickType",0);
				}
				json2.put("clickX", item.deminPlayer.clickX);
				json2.put("clickY", item.deminPlayer.clickY);
				json2.put("username", item.username);
				jsar1.add(json2);
			}
		}
		json1.put("preState", jsar1);
		String messages1 = json1.toString(); //传给已经完成click的玩家
		for (Player item : players) {
			try {
				//System.out.println(item.username);
				if (item.myWebsocket != null) {
					if (item.deminPlayer.hasClicked) {
						item.myWebsocket.session.getBasicRemote().sendText(messages1);
					} else {
						item.myWebsocket.session.getBasicRemote().sendText(messages2);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * 当前轮结束时进行批处理
	 */
	public void batchHandleTurn() { 
		ArrayList<GridPosition> zeroList = new ArrayList<GridPosition>(); //记录周边雷数为0的非雷格点的位置
		for (Player item : players) {
			int type = 2;
			if (item.deminPlayer.clickType) {
				type = 1;
			}
			int X = item.deminPlayer.clickX + 1;
			int Y = item.deminPlayer.clickY + 1;
			this.gridClicked[X][Y] = type;
			item.deminPlayer.hasClicked = false;
			if (this.isMine[X][Y]) {
				if (type == 1) { //将雷踩错时，则修改输出类型为10
					this.gridState[X][Y] = 10;
				}
			}
			if (this.clickRight(item)) {
				int countThis = 0; //记录点击这个地方 的人数
				for (Player item2: players) {
					if (item2.deminPlayer.clickX == item.deminPlayer.clickX && item2.deminPlayer.clickY == item.deminPlayer.clickY && this.clickRight(item2)) {
						countThis++;
					}
				}
				item.deminPlayer.score += this.scorePerGrid / countThis;
			}
			if (!this.isMine[X][Y] &&
					this.gridState[X][Y] == 0) {
				zeroList.add(new GridPosition(X, Y));
			}
		}
		synchronized (this.leftTime) {
			this.leftTime = this.maxTurnTime;
		}
		//打开0周边的方格
		while (true) {
			if (zeroList.isEmpty()) {
				break; //无法拓展时，停止搜索
			}
			GridPosition gptmp = zeroList.remove(0); //取出第一个元素进行处理
			int x = gptmp.girdX;
			int y = gptmp.gridY;
			for (int i = x-1; i <= x+1; i++) {
				for (int j = y-1; j <= y+1; j++) {
					if (i > 0 && i <= this.gridLen && j > 0 && j <= this.gridLen) {
						if (this.gridClicked[i][j] != 1 && this.gridClicked[i][j] != 2) { //还未打开过
							if (this.gridState[i][j] == 0) {
								zeroList.add(new GridPosition(i, j));
							}
						}
						this.gridClicked[i][j] = 1; //标记其已经被click了
					}
					
				}
			}
		}
		this.leftMine = this.calculateLeftMine(); //更新剩余的雷数
		this.finishedNum = 0; //完成人数置为0
		if (this.gameOver()) {
			this.gameStatus = 2; //检查游戏是否已经结束了
		}
		//System.out.println("leftNoneclick:"+this.leftNoneClicked());
		this.sendEndOfThisTurn();
		if (this.gameStatus == 2) { //游戏结束之后，将游戏结果返回给玩家
			this.setPointChange(); //将分数变化单独发过去
			sendAfterGame();
			/*
			 * 同时将该游戏房间玩家清空
			 */
			for (Player item: players) {
				item.nowGame = null;
			}
			players.clear();
			this.gameStatus = 0; //将房间恢复初始状态
			for (int i = 1; i < gridLen+1; i++) {
				for (int j= 1; j < gridLen+1; j++) {
					gridClicked[i][j] = 0; 
					this.gridState[i][j] = 0;
					this.isMine[i][j] = false; //默认都不是地雷
				}
			}
			this.generatingMine();
			this.gameStatus = 0; //恢复初始状态
		}
	}
	
	/*
	 * 游戏后返回玩家游戏结果(包括排名，积分变化等)
	 * (non-Javadoc)
	 * @see game.GameState#sendAfterGame()
	 */
	public void sendAfterGame() { 
		JSONObject json1 = new JSONObject();
		json1.put("action", 6); //6表示游戏结束时所发送的消息
		JSONArray jsar1 = new JSONArray(); //存储用户信息和排名
		for (Player item: players) {
			int rank = 1;
			for (Player item2: players) {
				if (item2.deminPlayer.score > item.deminPlayer.score) {
					rank++; //当有玩家排名比item高时，item的排名会向后移一位
				}
			}
			JSONObject json2 = new JSONObject();
			json2.put("username", item.username);
			json2.put("rank", rank);
			jsar1.add(json2);
		}
		json1.put("players", jsar1);
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
	 * 该轮游戏结束之后向玩家们发送消息
	 */
	public void sendEndOfThisTurn() {	
		JSONObject json1 = new JSONObject();
		json1.put("action", 4); //4表示扫雷该轮已经结束
		ArrayList<ArrayList> arr = new ArrayList<ArrayList>(); //记录棋局形态
		for (int i = 1; i < gridLen+1; i++) {
			ArrayList<Integer> arr1 = new ArrayList<Integer>();
			for (int j = 1; j < gridLen+1; j++) {
				if (this.gridClicked[i][j] == 0) { //还未点击时输出-1
					arr1.add(-1);
				} else {
					arr1.add(this.gridState[i][j]);
				}
			}
			arr.add(arr1);
		}
		JSONArray jsar1 = new JSONArray(); //记录玩家得分情况
		for (Player item: players) {
			JSONObject json2 = new JSONObject();
			json2.put("username", item.username);
			json2.put("score", item.deminPlayer.score);
			jsar1.add(json2);
		}
		json1.put("state", arr);
		json1.put("leftTime", this.leftTime);
		json1.put("leftMine", this.leftMine);
		json1.put("players", jsar1);
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
	
	public void revisiting(Player ply) { //处理有玩家回到游戏中的情况
		if (this.gameStatus == 0) return; //游戏还没开始时忽略请求
		for (Player item: this.players) {
			if (item.username.equals(ply.username)) {
				//System.out.println("Has send!");
				this.sendForGameState();
				this.sendEndOfThisTurn();
				this.sendForGameProcess();
			} else {
				//System.out.println("No send!");
			}
		}
	}
	
	public void handleLeftTimeZero() { //解决时间为零的情况
		if (this.gameStatus != 1) {
			return; //游戏为处在进行状态时直接跳过
		}
		for (Player item: players) {
			if (!item.deminPlayer.hasClicked) {
				synchronized (item) {
					if (item.deminPlayer.hasClicked) {
						continue;
					} else {
						item.deminPlayer.hasClicked = true;
					}
				}
				//进行自动操作处理
				int count = this.leftNoneClicked();
				Random ran = new Random(); //设置随机数种子
				int index = Math.abs(ran.nextInt()) % count; //随机选择一个位置
				for (int i = 0; i < this.gridLen*this.gridLen; i++) {
					if (this.gridClicked[i/this.gridLen+1][i%this.gridLen+1] != 1 &&
							this.gridClicked[i/this.gridLen+1][i%this.gridLen+1] != 2) {
						if (index == 0) {
							item.deminPlayer.clickX = i / this.gridLen;
							item.deminPlayer.clickY = i % this.gridLen;
							break;
						} else {
							index--;
						}
					}
				}
				int clickTypes = Math.abs(ran.nextInt()) % 2;
				if (clickTypes == 1) {
					item.deminPlayer.clickType = true;
				} else {
					item.deminPlayer.clickType = false;
				}
			}
		}
		this.finishedNum = this.gameNum;
		this.sendForGameProcess();
		this.batchHandleTurn();
	}
	
	protected void sendForMyGameState(JSONObject json) {
		json.put("totalMine", this.totalMine); //需要额外发送总地雷数
		json.put("gridLen", this.gridLen); //雷区的大小
		json.put("maxTime", this.maxTurnTime); //单轮最长时间
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
		int averageScore = 0; //记录平均分
		for (Player item: players) {
			averageScore += item.deminPlayer.score;
		}
		averageScore /= this.gameNum;
		/*
		 * 每个人的分数依据平均分进行变化，目标是使得所有人总的分数变化之和大于0
		 */
		JSONObject json1 = new JSONObject();
		json1.put("action", 11); // 11表示游戏结束后修改积分信息
		JSONArray jsar1 = new JSONArray(); //存储用户信息和排名
		for (Player item: players) {
			JSONObject json2 = new JSONObject();
			json2.put("username", item.username);
			int deltaPoint = (item.deminPlayer.score - averageScore + 2) / 3;
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
	
	class GridPosition {
		public int girdX; //1~gridLen
		public int gridY; //1~gridLen
		public GridPosition(int X, int Y) {
			this.girdX = X;
			this.gridY = Y;
		}
	}
	
}
