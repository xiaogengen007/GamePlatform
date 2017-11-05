package me.gacl.websocket;

public class DeminGame extends GameState{
	int[][] gridState = new int [8][8]; //0表示还未有人选中，1表示已选过，2表示正在有人选中
	DeminGame() {
		super();
		this.gameType = 1; //扫雷为类型1
	}
	
}
