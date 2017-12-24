package websocket;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import net.sf.json.JSONObject;
import player.Player;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import db.FriendManager;
import db.SetupDatabase;
import game.GameState;
import game.Timer;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint("/websocket")
public class WebSocket {
	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
	private static int onlineCount = 0;

	//concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
	public static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<WebSocket>();

	//与某个客户端的连接会话，需要通过它来给客户端发送数据
	public Session session;
	private static boolean startedthread = false;
	public Player myPlayer = null; //存储该用户的相关信息

	public WebSocket() {
/*		if (!this.startedthread) {
			new Thread(new SendMessage(this)).start();
			this.startedthread = true;
		} */
	}
	/**
	 * 连接建立成功调用的方法
	 * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
	 */
	@OnOpen
	public void onOpen(Session session){
		String encoding = System.getProperty("file.encoding");
		System.out.println(encoding);
		this.session = session;
		if (!Timer.hasSet) { //当第一个用户连接时即开始计时器线程
			new Thread(new Timer()).start();
			Timer.hasSet = true;
		}
		webSocketSet.add(this);     //加入set中
		addOnlineCount();           //在线数加1
		System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose(){
		boolean isSuccess = false;
		if (this.myPlayer != null && this.myPlayer.nowGame != null) {
			isSuccess = this.myPlayer.nowGame.deletePlayer(this.myPlayer); //试图删除在房间中记录
		}

		if (!isSuccess && this.myPlayer != null) { //未成功时将引用置为空
			this.myPlayer.myWebsocket = null;
		}
		webSocketSet.remove(this);  //从set中删除
		subOnlineCount();           //在线数减1
		System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 收到客户端消息后调用的方法
	 * @param message 客户端发送过来的消息
	 * @param session 可选的参数
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("来自客户端的消息:" + message);
		
		JSONObject json1 = JSONObject.fromObject(message);
		if (json1.getInt("action") == 1) { //1为登录或注册
			String name = (String) json1.getString("username");
			try {
				if (name.length() > 15 || name.length() <= 0) {
					this.sendMessage("");
				} else {
					this.myPlayer = GameState.searchFromName(name);
					if (myPlayer == null) {
						myPlayer = new Player(); //存储该用户的相关信息
						this.myPlayer.username = name;
						this.myPlayer.setupHashCode();
						this.myPlayer.myWebsocket = this; //建立player与websocket的双向连接
						//System.out.println("I'm not create new!");
					} else {
						//刷新该用户的界面，让他顺利回到游戏中
						this.myPlayer.myWebsocket = this; //建立player与websocket的双向连接
						this.myPlayer.nowGame.revisiting(this.myPlayer);
					}
					this.sendMessage(name+", hello for coming");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (json1.getInt("action") == 2) { //2为发消息
			String mymessage = json1.getString("message");
			//群发消息
			for(WebSocket item: webSocketSet){
				try {
					item.sendMessage(this.myPlayer.username +": "+ mymessage);
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		if (json1.getInt("action") == 3) { //3为玩扫雷时发消息
			if (!this.myPlayer.deminPlayer.hasClicked) {
				int clickX = json1.getInt("clickX"); //用户点击的X值(1-8)
				int clickY = json1.getInt("clickY"); //用户点击的Y值(1-8)
				int clickType = json1.getInt("clickType"); //点击类型，0表示左键，2表示右键
				if (this.myPlayer.nowGame != null) {
					this.myPlayer.nowGame.handleDemin(clickX, clickY, clickType, this);
				}
			}		
		}
		if (json1.getInt("action") == 4) { //4为游戏进入请求
			int requestNum = json1.getInt("type");
			if (requestNum == 1 || requestNum == 2) {
				GameState.distributeRoom(this.myPlayer, requestNum);
			}
		}
		if (json1.getInt("action") == 5) { //5为游戏中聊天
			if (this.myPlayer.nowGame != null) {
				String messages = this.myPlayer.username+": "+json1.getString("message");
				this.myPlayer.nowGame.handleForChating(messages);
			}
		}
		if (json1.getInt("action") == 6) { //6表示谁是卧底游戏中发送本轮发言
			if (this.myPlayer.nowGame != null) {
				String messages = json1.getString("message");
				this.myPlayer.nowGame.handleUndercover(messages, this);
			}
		}
		if (json1.getInt("action") == 7) {
			if (this.myPlayer.nowGame != null) {
				int userindex = json1.getInt("vote");
				this.myPlayer.nowGame.handleUndercoverVoting(userindex, this);
			}
		}
		if (json1.getInt("action") == 8) { //加好友请求
			String username1 = json1.getString("username1");
			String username2 = json1.getString("username2");
			if (!SetupDatabase.hasSet) {
				SetupDatabase.Setup();
			}
			if (this.myPlayer != null && this.myPlayer.nowGame != null) {
				this.myPlayer.nowGame.addFriendInRoom(username1, username2);
			}
			
		}
	}

	/**
	 * 发生错误时调用
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error){
		System.out.println("发生错误");
		error.printStackTrace();
	}

	/**
	 * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String message) throws IOException{
		JSONObject json1 = new JSONObject();
		json1.put("action", 2); //2表示发送消息
		json1.put("message", message);
		String messages = json1.toString();
		this.session.getBasicRemote().sendText(messages);
		//this.session.getAsyncRemote().sendText(message);
	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		WebSocket.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		WebSocket.onlineCount--;
	}
}
