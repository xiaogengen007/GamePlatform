var gridLen = 8;
var tMax = 20;
var tNow = tMax;

var MessageType = {
	status : 1,
	message : 2,
	state : 3,
	turnFinished : 4,
	chat : 5
}

//接收到消息的回调方法
websocket.onmessage = function (event) {
    var json1 = JSON.parse(event.data);
    console.log(json1.action);
    if (json1.action == MessageType.message) { //消息通讯
        if (json1.message != "惯例发送信息!") {
            setMessageInnerHTML(json1.message);
        }
    }
    else if (json1.action == 1) { //游戏状态的通讯
    	
    	for(var i = playerArray.length; i < json1.players.length; i++ ){
    		addPlayer(json1.players[i]);
    	}
    	
        if (json1.start == 0) {
            //游戏还未开始
            setMessageInnerHTML("Now has "+json1.players.length+" in this room, please wait for game start.");
        } else {
            //游戏已经开始
            setMessageInnerHTML("Game has started! Now has "+json1.players.length+" in this room.");
            startGame();
        }		
    }
    else if (json1.action == 3) { //游戏进行状态通讯
        if (json1.finished == 1) {
            setMessageInnerHTML("You have finished this turn click, now complete "+json1.finishNum+" of "+json1.playerNum);
        } else {
            setMessageInnerHTML("Please click for this turn, now complete "+json1.finishNum+" of "+json1.playerNum);
        }
        tNow = json1.leftTime;
        timeUpdate(tNow, tMax);
    }
    else if (json1.action == 4) { //游戏该轮完成后通讯
        for(var i = 0; i < gridLen; i++){
            for(var j = 0; j < gridLen; j++){
                $("#"+i+"-"+j).addClass("num").text(json1.state[i][j]);
            }
        }
        setMessageInnerHTML("End of this turn, please select for next turn.");
        tNow = 20;
        timeUpdate(tNow, tMax);
    }
    else if (json1.action == 5){
    	setMessageInnerHTML(json1.message);
    }
}

//发送消息
function send(e, x, y) {
    if (user == "") {
        alert("please login!");
    } else {
        if (e.which != 1 && e.which != 3) {
            return; //非单独按左右键时屏蔽消息
        }
        var json1 = {};
        json1.action = 3; //3表示正在进行扫雷游戏
        json1.clickX = x; //传输点击的位置
        json1.clickY = y;
        json1.clickType = e.which - 1;
        var messages = JSON.stringify(json1); 
        websocket.send(messages);
    }	
}

//更新时间进度条
function timeUpdate(timeNow, timeMax){
    var progressBar = document.getElementById('progressbar');
    var percentage = Math.floor(timeNow / timeMax * 100);
    progressBar.setAttribute('aria-valuenow',percentage.toString());
    progressBar.style.width = percentage + '%';
}

//开始游戏，更新html，引用对应的css
function startGame(){
	var node = document.createElement('link');
	node.rel = 'stylesheet';
	node.href = 'minesweeper.css';
	document.getElementsByTagName('head')[0].appendChild(node);
	
	$('#players').empty().removeClass('main').addClass('game');
	$('<div></div>').addClass('box').appendTo('#players');
	$('<div></div>').attr('id','progressbardiv').addClass('progress').appendTo('#players');
	
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
	
	
	var t = tMax;
	setInterval(function(){
		tNow--;
		timeUpdate(tNow, tMax);
	}, 1000)
}
