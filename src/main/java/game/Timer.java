package game;

import game.GameState;

//定时器，每一秒钟向所有的游戏进程发送更新指令
public class Timer implements Runnable{
	public static boolean hasSet = false; //初始化没有设定计时器
	public void refreshGames() { //更新各游戏状态的方法
		for (GameState gs: GameState.games) {
			gs.refreshTime();
		}
	}
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			refreshGames(); //试图更新游戏数据
			try {
				Thread.sleep(1000); //每次更新后沉睡1000ms
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

}
