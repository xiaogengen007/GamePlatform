//var playerNum = 4;
var isVoting = 0; //初始时不是投票环节
var state = new Array(8); //多生成一些防止不够用
var alive = new Array(8); //记录各玩家是否还活着,0表示已经死了，1表示还活着
var canVoted = new Array(8); //记录各玩家能否被投票
//var playerName = new Array(8);
var tMaxState;
var tMax;
var tNow;
var tMaxVote;
var timerCode;

//接收到消息的回调方法
websocket.onmessage = function (event) {
	var json1 = JSON.parse(event.data);
	if (json1.action == 1) { //游戏状态的通讯
		/*处理房间有人离开的情况
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
			setMessageInnerHTML("Now has "+json1.players.length+" in this room, please wait for game start.");
		} 
		if (json1.start == 1) {
			//游戏正在进行
			tMaxState = json1.maxTime;
			tMaxVote = json1.maxVoteTime;
			tMax = tMaxState;
			startGame();
			//playerNum = json1.players.length;
			setMessageInnerHTML("Game has started! Now has "+json1.players.length+" in this room.");
		}		
		if (json1.start == 2) {
			//游戏已经结束
			setMessageInnerHTML("Game has finished!");
			/*
			for (i=0; i<json1.players.length; i++) {
				for(j = 0; j < playerArray.length; j++){
					if(playerArray[j].username == json1.players[i].username){
						addRank(playerArray[j], json1.players[i].rank);
					}
				}
			}*/
			$('#myModal').modal({backdrop: 'static', keyboard: false});
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
			for (i=0; i<json1.preMessage.length; i++) {
				for (var j=0; j<playerArray.length; j++) {
					if (json1.preMessage[i].username == playerArray[j].username) {
						var tmp = $('#popmessage' + i).attr('data-content', json1.preMessage[i].message);
						tmp.popover('show');
					} 
				}	
			}
		}
		
	}
	if (json1.action == 4) { //游戏该轮完成后通讯
		//document.getElementById('message').innerHTML = "";
		var messages = JSON.stringify(json1);
		setMessageInnerHTML(messages);
		if (json1.resultType == 1) { //1 for can get the die player
			for (i=0; i<playerArray.length; i++) {
				$('#popmessage' + i).popover('hide');
			}
			isVoting = 0;
			tNow = tMaxState;
			tMax = tMaxState;
	        timeUpdate(tNow, tMax);
		} else { //否则则继续进行投票
			tNow = tMaxVote;
			tMax = tMaxVote
	        timeUpdate(tNow, tMax);
			for (i=0; i<playerArray.length; i++) {
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
		clearInterval(timerCode);
		timeUpdate(1,1);
		var messages = JSON.stringify(json1);
		setMessageInnerHTML(messages);
	}
	if (json1.action == 7) { //游戏状态的额外传输
		var keywords = json1.keyword;
		$("#keyword").text("您的关键词:"+keywords);
	}
	if (json1.action == 8) { //发言阶段结束时的发言记录
		isVoting = 1; //进入投票环节
		var messages = JSON.stringify(json1);
		setMessageInnerHTML(messages);
		for (i=0; i<playerArray.length; i++) {
			state[i] = ""; //初始化
		}
		for (i=0; i<json1.alive.length; i++) {
			alive[i] = json1.alive[i];
		}
		for (i=0; i<json1.messages.length; i++) {
			for (var j=0; j<playerArray.length; j++) {
				if (json1.messages[i].username == playerArray[j].username) {						
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
		for (i=0; i<playerArray.length; i++) {
			alive[i] = json1.baseInfo[i].alive;
		}
		if (json1.gameProcess == 1) { //在投票阶段需要根据情况来绘制投票器
			for (i = 0; i<playerArray.length; i++) {
				state[i] = json1.baseInfo[i].message;
			}
			for (i=0; i<playerArray.length; i++) {
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
    	json1.message = $('#stateInput').val();
    	var messages = JSON.stringify(json1);
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
	//alert("succeed vote "+playerName[num]);
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
	node.href = 'css/whoisundercover.css';
	document.getElementsByTagName('head')[0].appendChild(node);
	
	$('#players').empty();
	$('<div></div>').attr('id','mainArea').css('margin-top','50px').appendTo('#players');
	$('<div></div>').addClass("row placeholders").attr('id','playerlist').appendTo('#mainArea');
	
	for(var i = 1; i <= playerArray.length; i++){
		var popMessage = $('<div></div>').attr('data-container','body').attr('data-toggle','popover')
		.attr('data-placement','top').attr('data-content','word').attr('id','popmessage' + i);
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
	
	var div1 = $('<div></div>').addClass('row').attr('id','#keywordArea').appendTo('#mainArea');
	var div2 = $('<div></div>').addClass("col-md-6 col-md-offset-3 col-sm-12" )
	.attr('align','center').appendTo(div1);
	$('<h2></h>').text('请输入关键词').attr('id','keyword').appendTo(div2);
	var inputform = $('<div></div>').addClass('input-group').appendTo(div2);
	var inputbox = $('<input></input>').addClass('form-control')
	.attr('type','text').attr('id','stateInput').attr('placeholder','关键词').appendTo(inputform);
	var inputButtonGroup = $('<span></span>').addClass('input-groupp-btn').appendTo(inputform);
	var inputButton = $('<button></button>').addClass("btn btn-default").attr('type','button').text('确定').appendTo(inputButtonGroup);

	timerCode = setInterval(function(){
		tNow--;
		timeUpdate(tNow, tMax);
	}, 1000)
}