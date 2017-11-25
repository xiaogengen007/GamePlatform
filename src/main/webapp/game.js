var user = getParam("username");

var playerArray = Array();

//连接发生错误的回调方法
websocket.onerror = function () {
    setMessageInnerHTML("WebSocket连接发生错误1");
};

//连接成功建立的回调方法,TODO:选择游戏
websocket.onopen = function () {
    setMessageInnerHTML("WebSocket连接成功2");
    senduser();
    sendPlayRequest(); //发送玩游戏请求（扫雷）
}

//连接关闭的回调方法
websocket.onclose = function () {
    setMessageInnerHTML("WebSocket连接关闭3");
}

//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
window.onbeforeunload = function () {
    closeWebSocket();
}

function addMessage(){
	json1 = {};
	json1.action = 5; //5表示游戏过程中聊天
	json1.message = $('#testinput').val();
	var messages = JSON.stringify(json1);
    websocket.send(messages);//send message
}

//将消息显示在网页上
function setMessageInnerHTML(innerHTML) {
	$('<p></p>').append(innerHTML).appendTo('#messagebox');
    var div = document.getElementById('messagebox');
    div.scrollTop = div.scrollHeight;
}

//关闭WebSocket连接
function closeWebSocket() {
    websocket.close();
}

//发送你的用户名
function senduser() {
    var json1 = {};
    json1.action = 1; //1表示登录
    json1.username = user;
    var messages = JSON.stringify(json1);
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

function addPlayer(player){
	player.score = 0;
	player.thumbnail = "data:image/gif;base64,R0lGODlhAQABAIAAAHd3dwAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==";
	playerArray.push(player);
	$('#player' + playerArray.length + ' > h4').text(player.username);
}