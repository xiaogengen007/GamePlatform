var user = getParam("username");
var gameCode = parseInt(getParam("game"));
gameName = ['', '扫雷', '谁是卧底'];
gameNameEng = ['','minesweeper','whoisundercover'];
document.getElementById('roomtitle').innerText = gameName[gameCode];
var oHead = document.getElementsByTagName('body').item(0); 
var gameScript= document.createElement("script"); 
gameScript.type = "text/javascript";
gameScript.src = "js/"+ gameNameEng[gameCode] + ".js"; 
document.getElementsByTagName('body')[0].appendChild(gameScript); 

var playerArray = Array();

//连接发生错误的回调方法
websocket.onerror = function () {
    setMessageInnerHTML("WebSocket连接发生错误1");
};

//连接成功建立的回调方法,TODO:选择游戏
websocket.onopen = function () {
    setMessageInnerHTML("WebSocket连接成功2");
    senduser();
    sendPlayRequest(gameCode); //发送玩游戏请求
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

function sendPlayRequest(gameCode) {
    var json1 = {};
    json1.action = 4; //4表示请求加入游戏
    json1.type = gameCode; //type为1表示扫雷游戏
    var messages = JSON.stringify(json1);
    websocket.send(messages);
    setMessageInnerHTML("have send play request.");
}

function addPlayer(players){
	//player.thumbnail = "data:image/gif;base64,R0lGODlhAQABAIAAAHd3dwAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==";
	var newPlayerArray = Array();
	var i = 0;
	for(; i < playerArray.length; i++){
		if(players.length == i){
			for(var j = players.length;j < playerArray.length; j++){
				$('#player' + (j+1) + ' > img').css('opacity','0.0');
				$('#player' + (j+1) + ' > h4').text('');
			}
			break;
		}
		else if(playerArray[i].username != players[i].username){
			players[i].thumbnail = new Identicon(players[i].hashcode, 200).toString();
			players[i].thumbnail = 'data:image/png;base64,' + players[i].thumbnail;
			players[i].score = 0;
			newPlayerArray.push(players[i]);
			$('#player' + (i+1) + ' > h4').text(players[i].username);
			$('#player' + (i+1) + ' > img').css('opacity','1.0');
			$('#player' + (i+1) + ' > img').attr('src',players[i].thumbnail);
		}
		else{
			newPlayerArray.push(playerArray[i]);
		}
	}
	
	if(i < players.length){
		for(; i < players.length; i++){
			players[i].thumbnail = new Identicon(players[i].hashcode, 200).toString();
			players[i].thumbnail = 'data:image/png;base64,' + players[i].thumbnail;
			players[i].score = 0;
			newPlayerArray.push(players[i]);
			$('#player' + (i+1) + ' > h4').text(players[i].username);
			$('#player' + (i+1) + ' > img').css('opacity','1.0');
			$('#player' + (i+1) + ' > img').attr('src',players[i].thumbnail);
		}
	}
	
	playerArray = newPlayerArray;
}

function addRank(player, rank){
	var c1 = $('<span></span>').attr('title',rank.toString()).addClass('column1').text(rank.toString());
	var img = $('<img></img>').attr('src', player.thumbnail).attr('width', '40').attr('height', '40');
	var c2 = $('<span></span>').addClass('column2').append(img);
	var c3 = $('<span></span>').addClass('column3').text(player.username);
	var c4 = $('<span></span>').addClass('column4').text(player.score.toString());
	$('<section></section>').addClass('rank').append(c1).append(c2).append(c3).append(c4).appendTo('#gdt');
}