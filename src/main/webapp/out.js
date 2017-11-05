alert("OK");
var websocket = null;
//判断当前浏览器是否支持WebSocket
if ('WebSocket' in window) {
	websocket = new WebSocket("ws://59.66.124.167:8080/JavaWebSocket/websocket");
}
else {
	alert('当前浏览器 Not support websocket')
}
