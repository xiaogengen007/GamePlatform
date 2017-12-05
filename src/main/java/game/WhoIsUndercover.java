package game;

import net.sf.json.JSONObject;
import player.Player;

public class WhoIsUndercover extends GameState{
	String friendString = ""; //友方的词汇
	String undercoverString = ""; //卧底的词汇
	WhoIsUndercover() {
		super();
		this.gameType = 2; //2表示谁是卧底
		this.maxTurnTime = 30;
		this.leftTime = new Integer(this.maxTurnTime);
		this.friendString = "大佬";
		this.undercoverString = "大神";
	}
	
	public void initPlayers() { //初始化玩家的操作
		for (Player item: players) {
			item.ucPlayer.setPlayer();
		}
	}
	
	public void sendForMyGameState(JSONObject json) {
		json.put("maxTime", this.maxTurnTime); //单轮最长时间
	}
}
