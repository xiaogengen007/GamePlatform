package me.gacl.websocket;

import java.io.IOException;

public class SendMessage implements Runnable{
	
	WebSocketTest wst1;
	
	public SendMessage(WebSocketTest w1) {
		this.wst1 = w1;
	}
	
	public void run() {
		while (true) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(WebSocketTest item: wst1.webSocketSet){
				try {
					item.sendMessage("惯例发送信息!");
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		
	}

}
