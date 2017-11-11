var user = getParam("username");
var gridLen = 8;

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
            startGame();
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
        for(var i = 0; i < gridLen; i++){
            for(var j = 0; j < gridLen; j++){
                $("#"+i+"-"+j).addClass("num").text(json1.state[i][j]);
            }
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

function addMessage(){
	setMessageInnerHTML($('#testinput').val());
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

//发送消息
function send(e, x, y) {
    if (user == "") {
        alert("please login!");
    } else {
        //var e=window.event;//获取事件对象
        //var clickType=e.button;
        if (e.which != 1 && e.which != 3) {
            return; //非单独按左右键时屏蔽消息
        }
           //document.getElementById('ptest').innerHTML += x+" "+y+" ";
        var json1 = {};
        json1.action = 3; //3表示正在进行扫雷游戏
        json1.clickX = x; //传输点击的位置
        json1.clickY = y;
        json1.clickType = e.which - 1;
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

function timeUpdate(timeNow, timeMax){
    var progressBar = document.getElementById('progressbar');
    var percentage = Math.floor(timeNow / timeMax * 100);
    progressBar.setAttribute('aria-valuenow',percentage.toString());
    progressBar.style.width = percentage + '%';
}

function startGame(){
	var node = document.createElement('link');
	node.rel = 'stylesheet';
	node.href = 'minesweeper.css';
	document.getElementsByTagName('head')[0].appendChild(node);
	
	$('#players').empty().removeClass('main').addClass('game');
	$('<div></div>').addClass('box').appendTo('#players');
	$('<div></div>').attr('id','progressbardiv').addClass('progress').appendTo('#players');
	//$('<p></p>').text('test').appendTo('#progressbardiv');
	//document.getElementById('progressbardiv').innerHTML = 
	
	$('<div></div>').attr('id','progressbar')
	.addClass('progress-bar progress-bar-striped active')
	.attr('role', 'progressbar').attr('aria-valuenow', '100')
	.attr('aria-valuemin','0').attr('aria-valuemax','100').css('width','100%')
	.appendTo('#progressbardiv');
	
	$(".box").empty();
	for(var i=0;i<gridLen;i++){
		for(var j=0;j<gridLen;j++){
			$("<div></div>").addClass("block")
			.data("pos",{x:i,y:j}).attr("id",i+"-"+j)
			.mousedown(mousedownhandler).appendTo(".box");
		}
	}
	//$(document).on("contextmenu",false);  //右击浏览器弹出窗口事件 被 contextmenu事件冲掉
	function mousedownhandler(e){
		e.preventDefault();
		pos = $(this).attr('id').split('-');
		send(e, pos[0], pos[1]);
	}
}