package me.gacl.websocket;

import java.io.IOException;
import java.util.ArrayList;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

public class DeminGame extends GameState{
	int gridLen = 8; //雷区的大小
	int[][] gridState = new int [gridLen][gridLen]; //0表示还未有人选中，1表示已选过，2表示正在有人选中
	DeminGame() {
		super();
		this.gameType = 1; //扫雷为类型1
		for (int i = 0; i < gridLen; i++) {
			for (int j= 0; j < gridLen; j++) {
				gridState[i][j] = 0; 
			}
		}
	}
	public void HandleDemin(int clickX, int clickY, int clickType, WebSocket ws) { //完成扫雷游戏中的用户响应
		//System.out.println("123");
		if (this.isStarted && !ws.myPlayer.hasClicked && this.gridState[clickX][clickY] != 1) { //该方格还未选过时响应该次选方格
			ws.myPlayer.clickX = clickX;
			ws.myPlayer.clickY = clickY;
			if (clickType == 0) {
				ws.myPlayer.clickType = true;
			} else {
				ws.myPlayer.clickType = false;
			}
			ws.myPlayer.hasClicked = true; //完成该轮扫雷工作
			this.finshedNum++;
			if (this.finshedNum < this.gameNum) {
				sendForGameProcess(); //该轮未结束时告知用户进展情况
			} else {
				endOfThisTurn(); //完成此轮后进行一些常规操作
			}
		}	
	}
	public void sendForGameProcess() {
		JSONObject json1 = new JSONObject();
		json1.put("action", 3); //3表示扫雷该轮仍处于进行状态
		json1.put("finished", 1); //1表示已经完成
		json1.put("playerNum", players.size());
		json1.put("finishNum", this.finshedNum);
		String messages1 = json1.toString();
		json1.put("finished", 0);
		String messages2 = json1.toString();
		for (WebSocket item : players) {
			try {
				if (item.myPlayer.hasClicked) {
					item.session.getBasicRemote().sendText(messages1);
				} else {
					item.session.getBasicRemote().sendText(messages2);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void endOfThisTurn() {
		for (WebSocket item : players) {
			int type = 2;
			if (item.myPlayer.clickType) {
				type = 1;
			}
			this.gridState[item.myPlayer.clickX][item.myPlayer.clickY] = type;
			item.myPlayer.hasClicked = false;
		}
		this.finshedNum = 0; //完成人数置为0
		JSONObject json1 = new JSONObject();
		json1.put("action", 4); //4表示扫雷该轮已经结束
		ArrayList<ArrayList> arr = new ArrayList<ArrayList>();
		for (int i = 0; i < gridLen; i++) {
			ArrayList<Integer> arr1 = new ArrayList<Integer>();
			for (int j = 0; j < gridLen; j++) {
				arr1.add(this.gridState[i][j]);
			}
			arr.add(arr1);
		}
		json1.put("state", arr);
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
