alert("Welcome");
var websocket = null;
//判断当前浏览器是否支持WebSocket
if ('WebSocket' in window) {
	websocket = new WebSocket("ws://localhost:8080/JavaWebSocket/websocket");
}
else {
	alert('当前浏览器 Not support websocket')
}
