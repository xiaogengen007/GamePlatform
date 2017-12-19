var playerNum = 4;
var isVoting = 0; //初始时不是投票环节
var state = new Array(8); //多生成一些防止不够用
var alive = new Array(8); //记录各玩家是否还活着,0表示已经死了，1表示还活着
var canVoted = new Array(8); //记录各玩家能否被投票
var playerName = new Array(8);

//接收到消息的回调方法
websocket.onmessage = function (event) {
	var json1 = JSON.parse(event.data);
	if (json1.action == 1) { //游戏状态的通讯
		var playerInfo = "";
		for (i=0; i<json1.players.length; i++) {
			playerInfo += json1.players[i].username+" ";
		}
		/*
		for(var i = 0; i < json1.players.length; i++){
			if(json1.players[i].username == playerArray[i].username){
				continue;
			}
			
			if(i > playerArray.length - 1){
				addPlayer(json1.players[i]);
			}
			
		}
		*/
		for(var i = playerArray.length; i < json1.players.length; i++ ){
    		addPlayer(json1.players[i]);
    	}
		
		if (json1.start == 0) {
			//游戏还未开始
			setMessageInnerHTML("Now has "+playerInfo+" in this room, please wait for game start.");
		} 
		if (json1.start == 1) {
			//游戏正在进行
			startGame();
			playerNum = json1.players.length;
			for (i=0; i<json1.players.length; i++) {
				playerName[i] = json1.players[i].username;
			}
			setMessageInnerHTML("Game has started! Now has "+playerInfo+" in this room.");
		}		
		if (json1.start == 2) {
			//游戏已经结束
			setMessageInnerHTML("Game has finished!");
		}
	}
	if (json1.action == 2) { //消息通讯
		if (json1.message != "惯例发送信息!") {
			setMessageInnerHTML(json1.message);
		}
	}
	if (json1.action == 3) { //游戏进行状态通讯
		var messages = JSON.stringify(json1);
		setMessageInnerHTML(messages);
		if (json1.finished != 0) { //不是为非完成的用户
			for (i=0; i<playerNum; i++) {
				state[i] = "";
			}
			for (i=0; i<json1.preMessage.length; i++) {
				for (var j=0; j<playerNum; j++) {
					if (json1.preMessage[i].username == playerName[j]) {						
						state[j] = json1.preMessage[i].message;
					} 
				}	
			}
			writeNormal();
		}
		
	}
	if (json1.action == 4) { //游戏该轮完成后通讯
		//document.getElementById('message').innerHTML = "";
		var messages = JSON.stringify(json1);
		setMessageInnerHTML(messages);
		if (json1.resultType == 1) { //1 for can get the die player
			for (i=0; i<playerNum; i++) {
				state[i] = "";
			}
			writeNormal();
			isVoting = 0;
		} else { //否则则继续进行投票
			for (i=0; i<playerNum; i++) {
				canVoted[i] = 0;
			}
			for (i=0; i<json1.nextVoted.length; i++) {
				canVoted[json1.nextVoted[i].nextVotedIndex] = 1;
			}
			writeAfterSpeechProcess();
		}
		
	}
	if (json1.action == 5) { //游戏中进行聊天
		setMessageInnerHTML(json1.message);
	}
	if (json1.action == 6) { //游戏结束时的通讯
		var messages = JSON.stringify(json1);
		setMessageInnerHTML(messages);
	}
	if (json1.action == 7) { //游戏状态的额外传输
		var keywords = json1.keyword;
		//document.getElementById('keyword').innerHTML = "您的关键词："+keywords;
	}
	if (json1.action == 8) { //发言阶段结束时的发言记录
		isVoting = 1; //进入投票环节
		var messages = JSON.stringify(json1);
		setMessageInnerHTML(messages);
		for (i=0; i<playerNum; i++) {
			state[i] = ""; //初始化
		}
		for (i=0; i<json1.alive.length; i++) {
			alive[i] = json1.alive[i];
		}
		for (i=0; i<json1.messages.length; i++) {
			for (var j=0; j<playerNum; j++) {
				if (json1.messages[i].username == playerName[j]) {						
					state[j] = json1.messages[i].message;
					canVoted[j] = json1.messages[i].canVoted; //1为可以被投票，0为不可以
				} 
			}	
		}
		writeAfterSpeechProcess();	
	}
	if (json1.action == 9) { //投票环节的中间过程记录
		var messages = JSON.stringify(json1);
		setMessageInnerHTML(messages);
	}
	
	if (json1.action == 10) { //谁是卧底的额外信息
		var messages = JSON.stringify(json1);
		setMessageInnerHTML(messages);
		for (i=0; i<playerNum; i++) {
			alive[i] = json1.baseInfo[i].alive;
		}
		if (json1.gameProcess == 1) { //在投票阶段需要根据情况来绘制投票器
			for (i = 0; i<playerNum; i++) {
				state[i] = json1.baseInfo[i].message;
			}
			for (i=0; i<playerNum; i++) {
				canVoted[i] = 0;
			}
			for (i=0; i<json1.userVoted.length; i++) {
				canVoted[json1.userVoted[i].votedIndex] = 1;
			}
			writeAfterSpeechProcess();
		}
	}
}

//在非投票时更新界面
function writeNormal() {
	/*
	document.getElementById('ptest').innerHTML = "";
	for (i=0; i<playerNum; i++) {
    	document.getElementById('ptest').innerHTML += "<div>" +playerName[i]+":"+state[i]+"</div><br/>";
	}
	*/
}

//在发言阶段结束后更新界面
function writeAfterSpeechProcess() {
	/*
	document.getElementById('ptest').innerHTML = "";
	for (i=0; i<playerNum; i++) {
		if (alive[i] == 1 && canVoted[i] == 1) { //或者需要让他可以被投票
			document.getElementById('ptest').innerHTML += "<div>" +playerName[i]+":"+state[i]+"<button onmousedown=\"sendVote(" + i +")\">他是卧底 </button></div><br/>";
		} else {
			document.getElementById('ptest').innerHTML += "<div>" +playerName[i]+":"+state[i]+"</div><br/>";
		}
    	
	}*/
}

//发送消息
function send() {
	if (isVoting == 0) { //不是投票环节采允许发言
    	var json1 = {};
    	json1.action = 6; //6表示谁是卧底游戏过程中用户发送本轮发言
    	json1.message = document.getElementById('gametext').value;
    	var messages = JSON.stringify(json1);
    	//setMessageInnerHTML("myname:"+user);
    	websocket.send(messages);
	} else {
		alert("can not speech in voting process!");
	}
}

//发送投票信息
function sendVote(num) {
	var json1 = {};
	json1.action = 7; //7表示发送投票信息
	json1.vote = num; //num从0~playerNum-1,且只能投给活着的人
	var messages = JSON.stringify(json1);
	websocket.send(messages);
	alert("succeed vote "+playerName[num]);
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

//在游戏中发送消息
function sendGame() {
	var json1 = {};
	json1.action = 5; //5表示游戏过程中聊天
	json1.message = document.getElementById('text').value;
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
	json1.type = 2; //type为2表示谁是卧底游戏
	var messages = JSON.stringify(json1);
	websocket.send(messages);
	setMessageInnerHTML("have send play request.");
}

function startGame(){
	var node = document.createElement('link');
	node.rel = 'stylesheet';
	node.href = 'css/whoisundercover.css';
	document.getElementsByTagName('head')[0].appendChild(node);
	
	$('#players').empty();
	$('<div></div>').attr('id','mainArea').css('margin-top','50px').appendTo('#players');
	$('<div></div>').addClass("row placeholders").attr('id','playerlist').appendTo('#mainArea');
	
	for(var i = 1; i <= playerArray.length; i++){
		var popMessage = $('<div></div>').attr('data-container','body').attr('data-toggle','popover')
		.attr('data-placement','top').attr('data-content','word');
		var img = $('<img></img>').addClass("img-responsive")
		.attr('src',playerArray[i-1].thumbnail)
		.attr('height','200').attr('width','200').attr('alt',"Generic placeholder thumbnail");
		var label = $('<h6></h6>').text(playerArray[i-1].username);
		var scoreLabel = $('<h6></h6>').attr('id','score' + i).text(playerArray[i-1].score).css('color', '#F00');
		var classList;
		if(i == 1){
			classList = "col-md-2 col-md-offset-" + (6-playerArray.length) +" placeholder";
		}
		else{
			classList = "col-md-2 placeholder";
		}
		$('<div></div>').addClass(classList).attr('id','player' + i).append(popMessage)
		.append(img).append(label).append(scoreLabel).appendTo('#playerlist');
	}
	
	$('<div></div>').attr('id','progressbardiv').addClass('progress').appendTo('#mainArea');
	
	$('<div></div>').attr('id','progressbar')
	.addClass('progress-bar progress-bar-striped active')
	.attr('role', 'progressbar').attr('aria-valuenow', '100')
	.attr('aria-valuemin','0').attr('aria-valuemax','100').css('width','100%')
	.appendTo('#progressbardiv');
}