package websocket;

import java.io.IOException;

public class SendMessage implements Runnable{
	
	public SendMessage() {

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
			for(WebSocket item: WebSocket.webSocketSet){
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
