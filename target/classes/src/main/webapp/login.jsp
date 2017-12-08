<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>登录界面</title>
</head>
<body>
    Welcome<br/><input id="username" type="text"/>
    <button onclick="inMainWindow()">发送用户名</button>
    <br/>
    <div id="message"></div>
</body>
<body>

</body>
<script language="javascript" type="text/javascript">
	
	//发送你的用户名
    function senduser() {
    	user = document.getElementById('user').value;
    	var json1 = {};
    	json1.action = 1; //1表示登录
    	json1.username = user;
    	//json1.message = null;
    	var messages = JSON.stringify(json1);
    	//setMessageInnerHTML("myname:"+user);
    	websocket.send(messages);
    }
	function inMainWindow() {
		//window.location.href="index.jsp"+"?username="+document.getElementById('username').value;
		window.location.href="Demining.jsp"+"?username="+document.getElementById('username').value;
	}
</script>
</html>