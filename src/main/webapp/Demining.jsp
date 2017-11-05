<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>扫雷小游戏</title>
</head>
<body>
<div id="ptest"></div>


<script type="text/javascript">
	var i = 1,j=1;
	for (i=1; i<=8; i++) {
		for (j=1; j<=8; j++) {
    		document.getElementById('ptest').innerHTML += "<button onmousedown=\"send(" + i + "," + j +")\">" + i + j + "</button></div>";
		}
		document.getElementById('ptest').innerHTML += "<br/>";
	}
</script>
</body>
<div id="message"></div>
<script language="Javascript">
    document.oncontextmenu = function(){return false;} //禁止右键展开菜单项
</script>
<script language="Javascript">
	var state = new Array(8);
	var canClicked = 1; //1表示可以点击
	for (var i = 0; i < 8; i++) {
		state[i] = new Array(8);
	}
	for (var i = 0; i < 8; i++) {
		for (var j = 0; j < 8; j++) {
			state[i][j] = 0;
		}
		
	}
</script>
<script type="text/javascript" src="out.js"></script>
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
        sendPlayRequest(); //发送玩游戏请求（扫雷）
    }

    //接收到消息的回调方法
    websocket.onmessage = function (event) {
    	var json1 = JSON.parse(event.data);
		if (json1.action == 2) { //消息通讯
			if (json1.message != "惯例发送信息!") {
				setMessageInnerHTML(json1.message);
			}
		}
		if (json1.action == 1) { //游戏状态的通讯
			if (json1.start == 0) {
				//游戏还未开始
				setMessageInnerHTML("Now has "+json1.playerNum+" in this room, please wait for game start.");
			} else {
				//游戏已经开始
				setMessageInnerHTML("Game has started! Now has "+json1.playerNum+" in this room.");
			}		
		}
		if (json1.action == 3) { //游戏进行状态通讯
			if (json1.finished == 1) {
				setMessageInnerHTML("You have finished this turn click, now complete "+json1.finishNum+" of "+json1.playerNum);
			} else {
				setMessageInnerHTML("Please click for this turn, now complete "+json1.finishNum+" of "+json1.playerNum);
			}
		}
		if (json1.action == 4) { //游戏该轮完成后通讯
			document.getElementById('ptest').innerHTML = "";
			var i = 1,j=1;
			for (i=1; i<=8; i++) {
				for (j=1; j<=8; j++) {
		    		document.getElementById('ptest').innerHTML += "<button onmousedown=\"send(" + i + "," + j +")\">" + json1.state[i-1][j-1]+ "</button></div>";
				}
				document.getElementById('ptest').innerHTML += "<br/>";
			}
			setMessageInnerHTML("End of this turn, please select for next turn.");
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

    //发送消息
    function send(x,y) {
    	if (user == "") {
    		alert("please login!");
    	} else {
    		var e=window.event;//获取事件对象
    	    var clickType=e.button;
    		if (clickType != 0 && clickType != 2) {
    			return; //非单独按左右键时屏蔽消息
    		}
    	   	document.getElementById('ptest').innerHTML += x+" "+y+" ";
    		var json1 = {};
        	json1.action = 3; //3表示正在进行扫雷游戏
        	json1.clickX = x; //传输点击的位置
        	json1.clickY = y;
        	json1.clickType = clickType;
            var messages = JSON.stringify(json1); 
            websocket.send(messages);
    	}	
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
    	json1.type = 1; //type为1表示扫雷游戏
    	var messages = JSON.stringify(json1);
    	websocket.send(messages);
    	setMessageInnerHTML("have send play request.");
    }
</script>
</html>