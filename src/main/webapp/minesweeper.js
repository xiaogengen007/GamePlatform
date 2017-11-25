var gridLen;
var tMax;
var tNow;
var timerCode;

var MessageType = {
	status : 1,
	message : 2,
	state : 3,
	turnFinished : 4,
	chat : 5
};

var actionArray = Array();

//接收到消息的回调方法
websocket.onmessage = function (event) {
    var json1 = JSON.parse(event.data);
    if (json1.action == MessageType.message) { //消息通讯
        if (json1.message != "惯例发送信息!") {
            setMessageInnerHTML(json1.message);
        }
    }
    else if (json1.action == 1) { //游戏状态的通讯
    	
    	for(var i = playerArray.length; i < json1.players.length; i++ ){
    		addPlayer(json1.players[i]);
    	}
    	
    	gridLen = json1.gridLen;
        tMax = json1.maxTime;
        tNow = tMax;
        numMineUpdate(json1.totalMine);
    	
        if (json1.start == 0) {
            //游戏还未开始
            setMessageInnerHTML("Now has "+json1.players.length+" in this room, please wait for game start.");
        } 
        else if (json1.start == 1) {
            //游戏已经开始
            setMessageInnerHTML("Game has started! Now has "+json1.players.length+" in this room.");
            startGame();
        }
        else if (json1.start == 2) {
        	setMessageInnerHTML("Game has finished!");
        }
    }
    else if (json1.action == 6) { //游戏结束时的通讯
    	clearInterval(timerCode);
    	timeUpdate(1, 1);
		setMessageInnerHTML("This game has finished! Following is the rank:");
		
		for (i=0; i<json1.players.length; i++) {
			for(j = 0; j < playerArray.length; j++){
				if(playerArray[j].username == json1.players[i].username){
					addRank(playerArray[j], json1.players[i].rank);
				}
			}
		}
		$('#myModal').modal({backdrop: 'static', keyboard: false});
	}
    else if (json1.action == 3) { //游戏进行状态通讯
        if (json1.finished == 1) {
            setMessageInnerHTML("You have finished this turn click, now complete "+json1.finishNum+" of "+json1.playerNum);
        } else {
            setMessageInnerHTML("Please click for this turn, now complete "+json1.finishNum+" of "+json1.playerNum);
        }
        tNow = json1.leftTime;
        timeUpdate(tNow, tMax);
        console.log(json1.preState.length);
        for(var i = actionArray.length; i < json1.preState.length; i++){
        	actionArray.push(json1.preState[i]);
        	var thumbnail;
        	for(var j = 0; j < playerArray.length; j++){
        		if(playerArray[j].username == json1.preState[i].username){
        			thumbnail = playerArray[j].thumbnail;
        		}
        	}
        	
        	var img = $('<img></img>').addClass("img-responsive")
    		.attr('src',thumbnail)
    		.attr('height','16').attr('width','16').attr('alt',"Generic placeholder thumbnail");
    		$('<div></div>').addClass("placeholder").css('float','left')
    		.append(img).appendTo("#"+json1.preState[i].clickX+"-"+json1.preState[i].clickY);
        }
    }
    else if (json1.action == 4) { //游戏该轮完成后通讯
    	
    	for(var i = 0; i < actionArray.length; i++){
    		$("#"+actionArray[i].clickX+"-"+actionArray[i].clickY).empty();
    	}
    	
        for(var i = 0; i < gridLen; i++){
            for(var j = 0; j < gridLen; j++){
            	if(json1.state[i][j] >= 0){
            		if(json1.state[i][j] == 0){
            			$("#"+i+"-"+j).addClass("num").text('');
            		}
            		else if (json1.state[i][j] < 9){
            			$("#"+i+"-"+j).addClass("num").text(json1.state[i][j]);
            		}
            		else if (json1.state[i][j] == 9){
            			$("#"+i+"-"+j).addClass("num").text('△');
            		}
            		else{
            			$("#"+i+"-"+j).addClass("num").text('✸');
            		}
            	}
                
            }
        }
        setMessageInnerHTML("End of this turn, please select for next turn.");
        tNow = tMax;
        timeUpdate(tNow, tMax);
        actionArray.splice(0, actionArray.length);
        numMineUpdate(json1.leftMine);
    	
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

function numMineUpdate(numMine){
	$('.flagbox').text('剩余地雷: ' + numMine);
}

//开始游戏，更新html，引用对应的css
function startGame(){
	var node = document.createElement('link');
	node.rel = 'stylesheet';
	node.href = 'minesweeper.css';
	document.getElementsByTagName('head')[0].appendChild(node);
	
	node = document.createElement('link');
	node.rel = 'stylesheet';
	node.href = 'loading.css';
	document.getElementsByTagName('head')[0].appendChild(node);
	
	$('#players').empty().removeClass('main').addClass('game');
	$('<div></div>').addClass('box').appendTo('#players');
	$('<div></div>').addClass('flagbox').appendTo('#players');
	$('<div></div>').attr('id','progressbardiv').addClass('progress').appendTo('#players');
	
	$('<div></div>').attr('id','progressbar')
	.addClass('progress-bar progress-bar-striped active')
	.attr('role', 'progressbar').attr('aria-valuenow', '100')
	.attr('aria-valuemin','0').attr('aria-valuemax','100').css('width','100%')
	.appendTo('#progressbardiv');
	
	$('<div></div>').addClass('row placeholders').attr('id','playerlist').appendTo('#players');
	
	$(".box").empty();
	for(var i=0;i<gridLen;i++){
		for(var j=0;j<gridLen;j++){
			//var innerBlock = $("<div></div>").addClass('row');
			
			$("<div></div>").addClass("block")
			.data("pos",{x:i,y:j}).attr("id",i+"-"+j)
			.mousedown(mousedownhandler).
			appendTo(".box");
		}
	}
	
	for(var i = 1; i <= playerArray.length; i++){
		var img = $('<img></img>').addClass("img-responsive")
		.attr('src',playerArray[i-1].thumbnail)
		.attr('height','200').attr('width','200').attr('alt',"Generic placeholder thumbnail");
		var label = $('<h4></h4>').text(playerArray[i-1].username);
		var object1 = $('<div></div>').addClass('object').attr('id','object_one');
		var object2 = $('<div></div>').addClass('object').attr('id','object_two');
		var object3 = $('<div></div>').addClass('object').attr('id','object_three');
		var loading = $('<div></div>').attr('id','loading').append(object1).append(object2).append(object3);
		$('<div></div>').addClass("col-xs-3 col-sm-1 placeholder").attr('id','player' + i)
		.append(loading).append(img).append(label).appendTo('#playerlist');
	}
	
	function mousedownhandler(e){
		e.preventDefault();
		pos = $(this).attr('id').split('-');
		send(e, pos[0], pos[1]);
	}
	
	timerCode = setInterval(function(){
		tNow--;
		timeUpdate(tNow, tMax);
	}, 1000)
}
