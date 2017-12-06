<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>谁是卧底小游戏</title>
</head>
<body>
<div id="ptest"></div>


<script type="text/javascript">
	var playerNum = 4;
	var state = new Array(8); //多生成一些防止不够用
	var playerName = new Array(8);
	// firefox does not support window.event
	if(navigator.userAgent.indexOf('Firefox') >= 0)
	{
   		var $E = function(){var c=$E.caller; while(c.caller)c=c.caller; return c.arguments[0]};
   		window.__defineGetter__("event", $E);
	}

	var i = 1,j=1;
	for (i=1; i<=playerNum; i++) {
    	document.getElementById('ptest').innerHTML += "<div>" + i + "</div><br/>";
	}
</script>
</body>
<hr/>
<div id="keyword">您的关键词：</div>
<input id="gametext" type="text"/>
<button onclick="send()">本轮发言</button>
<hr/>
<input id="text" type="text"/>
<button onclick="sendGame()">聊天</button>
<hr/>
<div id="message"></div>
<script language="Javascript">
    document.oncontextmenu = function(){return false;} //禁止右键展开菜单项
</script>
<script language="Javascript">
	
	

</script>
<script type="text/javascript" src="js/out.js"></script>
<script type="text/javascript">   
    var user = getParam("username");  
    
    //连接发生错误的回调方法
    websocket.onerror = function () {
        setMessageInnerHTML("WebSocket连接发生错误1");
    };

    //连接成功建立的回调方法
    websocket.onopen = function () {
        setMessageInnerHTML("WebSocket连接成功2");
        senduser();
        sendPlayRequest(); //发送玩游戏请求（谁是卧底）
    }

    //接收到消息的回调方法
    websocket.onmessage = function (event) {
    	var json1 = JSON.parse(event.data);
		if (json1.action == 1) { //游戏状态的通讯
			var playerInfo = "";
			for (i=0; i<json1.players.length; i++) {
				playerInfo += json1.players[i].username+" ";
			}	
			if (json1.start == 0) {
				//游戏还未开始
				setMessageInnerHTML("Now has "+playerInfo+" in this room, please wait for game start.");
			} 
			if (json1.start == 1) {
				//游戏正在进行
				playerNum = json1.players.length;
				for (i=0; i<json1.players.length; i++) {
					playerName[i] = json1.players[i].username;
				}
				setMessageInnerHTML("Game has started! Now has "+playerInfo+" in this room.");
			}		
			if (json1.start == 2) {
				//游戏已经结束
				setMessageInnerHTML("Game has finished!");
			}
		}
		if (json1.action == 2) { //消息通讯
			if (json1.message != "惯例发送信息!") {
				setMessageInnerHTML(json1.message);
			}
		}
		if (json1.action == 3) { //游戏进行状态通讯
			var messages = JSON.stringify(json1);
			setMessageInnerHTML(messages);
			if (json1.finished != 0) { //不是为非完成的用户
				for (i=0; i<playerNum; i++) {
					state[i] = "";
				}
				for (i=0; i<json1.preMessage.length; i++) {
					for (var j=0; j<playerNum; j++) {
						if (json1.preMessage[i].username == playerName[j]) {						
							state[j] = json1.preMessage[i].message;
						} 
					}	
				}
				writeNormal();
			}
			
		}
		if (json1.action == 4) { //游戏该轮完成后通讯
			document.getElementById('ptest').innerHTML = "";
			var i = 1,j=1;
			for (i=1; i<=gridLen; i++) {
				for (j=1; j<=gridLen; j++) {
		    		document.getElementById('ptest').innerHTML += "<button onmousedown=\"send(" + i + "," + j +")\">" + json1.state[i-1][j-1]+ "</button></div>";
				}
				document.getElementById('ptest').innerHTML += "<br/>";
			}
			document.getElementById('message').innerHTML = "";
 			setMessageInnerHTML("End of this turn, please select for next turn, now left "+json1.leftTime+" seconds with "+json1.leftMine +" left mines.");
			var scores = "";
			for (i=0; i<json1.players.length; i++) {
				scores += json1.players[i].username+":"+json1.players[i].score+" ";
			}
 			setMessageInnerHTML(scores);
		}
		if (json1.action == 5) { //游戏中进行聊天
			setMessageInnerHTML(json1.message);
		}
		if (json1.action == 6) { //游戏结束时的通讯
			setMessageInnerHTML("This game has finished! Following is the rank:");
			var ranks = "";
			for (i=0; i<json1.players.length; i++) {
				ranks = json1.players[i].username + ": " + json1.players[i].rank;
				setMessageInnerHTML(ranks);
			}
		}
		if (json1.action == 7) { //游戏状态的额外传输
			var keywords = json1.keyword;
			document.getElementById('keyword').innerHTML = "您的关键词："+keywords;
		}
    }

    //连接关闭的回调方法
    websocket.onclose = function () {
        setMessageInnerHTML("WebSocket连接关闭3");
    }

    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function () {
        closeWebSocket();
    }

    //将消息显示在网页上
    function setMessageInnerHTML(innerHTML) {
        document.getElementById('message').innerHTML += innerHTML + '<br/>';
    }

    //关闭WebSocket连接
    function closeWebSocket() {
        websocket.close();
    }
 
    //在非投票时更新界面
    function writeNormal() {
    	document.getElementById('ptest').innerHTML = "";
    	for (i=0; i<playerNum; i++) {
        	document.getElementById('ptest').innerHTML += "<div>" +playerName[i]+":"+state[i]+"</div><br/>";
    	}
    }
    
    
    //发送消息
    function send() {
    	var json1 = {};
    	json1.action = 6; //6表示谁是卧底游戏过程中用户发送本轮发言
    	json1.message = document.getElementById('gametext').value;
    	var messages = JSON.stringify(json1);
    	//setMessageInnerHTML("myname:"+user);
    	websocket.send(messages);
    }
    
    //发送你的用户名
    function senduser() {
    	var json1 = {};
    	json1.action = 1; //1表示登录
    	json1.username = user;
    	var messages = JSON.stringify(json1);
    	//setMessageInnerHTML("myname:"+user);
    	websocket.send(messages);
    }
    
    //在游戏中发送消息
    function sendGame() {
    	var json1 = {};
    	json1.action = 5; //5表示游戏过程中聊天
    	json1.message = document.getElementById('text').value;
    	var messages = JSON.stringify(json1);
    	//setMessageInnerHTML("myname:"+user);
    	websocket.send(messages);
    }
    
    function getParam(paramName) {
        paramValue = "";
        isFound = false;
        if (this.location.search.indexOf("?") == 0 && this.location.search.indexOf("=") > 1) {
            arrSource = unescape(this.location.search).substring(1, this.location.search.length).split("&");
            i = 0;
            while (i < arrSource.length && !isFound) {
                if (arrSource[i].indexOf("=") > 0) {
                    if (arrSource[i].split("=")[0].toLowerCase() == paramName.toLowerCase()) {
                        paramValue = arrSource[i].split("=")[1];
                        isFound = true;
                    }
                }
                i++;
            }
        }
        return paramValue;
    }
    
    function sendPlayRequest() {
    	var json1 = {};
    	json1.action = 4; //4表示请求加入游戏
    	json1.type = 2; //type为2表示谁是卧底游戏
    	var messages = JSON.stringify(json1);
    	websocket.send(messages);
    	setMessageInnerHTML("have send play request.");
    }
</script>
</html>