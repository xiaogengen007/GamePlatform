<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Java后端WebSocket的Tomcat实现</title>
</head>
<body>
    Welcome<br/>
    <input id="text" type="text"/>
    <button onclick="send()">发送消息</button>
    <hr/>
    <button onclick="closeWebSocket()">关闭WebSocket连接</button>
    <hr/>
    <div id="message"></div>
</body>
<script type="text/javascript" src="out.js"></script>
<script type="text/javascript">   
    var user = getParam('username');  
    //连接发生错误的回调方法
    websocket.onerror = function () {
        setMessageInnerHTML("WebSocket连接发生错误1");
    };

    //连接成功建立的回调方法
    websocket.onopen = function () {
        setMessageInnerHTML("WebSocket连接成功2");
        senduser();
    }

    //接收到消息的回调方法
    websocket.onmessage = function (event) {
    	//setMessageInnerHTML("接收到消息啦！");
    	var json1 = JSON.parse(event.data);
    	if (json1.action == 2) {
    		if (json1.message == "") {     
            	alert("name is invalid!");
            	window.location.href="login.jsp";    	
            } else {
            	setMessageInnerHTML(event.data);
            }
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
    function send() {
    	if (user == "") {
    		alert("please login!");
    	} else {
    		var json1 = {};
        	json1.action = 2; //2表示发送消息
        	json1.message = document.getElementById('text').value;
            var messages = JSON.stringify(json1); 
            websocket.send(messages);
    	}	
    }
    
    //发送你的用户名
    function senduser() {
    	//user = document.getElementById('user').value;
    	var json1 = {};
    	json1.action = 1; //1表示登录
    	json1.username = user;
    	//json1.message = null;
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
</script>
</html>