# GuideLine to This Program

### the communication standard

- protocol: websocket


- text format: JSON

##### Server to Browser

| action | function             | part                              |
| ------ | -------------------- | --------------------------------- |
| 1      | game status          | start,players,(...)               |
| 2      | send message         | message                           |
| 3      | state in game        | finished,leftTime,playerNum,(...) |
| 4      | finish one turn      |                                   |
| 5      | chat in game         | message                           |
| 6      | game result          | players                           |
| 7      | other game state     |                                   |
| 8      | finish one process   |                                   |
| 9      | state in one process |                                   |
| 10     | handle for revisit   |                                   |
| 11     | send for game score  |                                   |
| 12     | add friend           | username1, username2, result      |

more information

##### action 1: 

- for all game:

  - players(array,store the players info in the room) ,for players[i]
    - username
    - hashcode (to generate his icon)

- for demining game:

  - totalMine
  - gridLen
  - maxTime

- for undercover game:

  - maxTime
  - maxVoteTime

  ​



##### action 3:

- for demining game:
  - finishNum
  - preState(array, only to the players finished this turn), for preState[i]:
    - username
    - clickType(1 for click left button, 0 for click right button)
    - clickX(0~7)
    - clickY(0~7)


- for undercover game:
  - aliveNum
  - submitNum
  - preMessage(array, only to the players submitted for this turn or has not been alive), for preMessage[i]
    - username
    - message



##### action 4: 

- for demining game:

  - state(2 dim vector, for the grids' state)
  - state[i]\[j](an integer): 0~8 for the neighbor bomb's number, 9 for rightly signing the bomb, 10 for wrongly clicking the bomb, -1 for none clicked this grid
  - players(array, to tell the players' score), for players[i]:
    - username
    - score

- for undercover game:

  - resultType (1 or 2, 1 is for can get the die player, 2 for cannot get)


  - (for resultType = 1)
    - diePlayer (his username)
    - voteResult (an array, to get the result of every one voted count)
      - votedName
      - votedNum
- (for resultType = 2)
  - voteResult (an array, to get the result of every one voted count)
    - votedName
    - votedNum
  - nextVoted (an array, to get which will be voted in the next vote turn)
    - nextVotedIndex (the voted player index)
  - nextVote (an array, to get who can vote in the next vote turn)
    - nextVoteIndex (the vote player index)



##### action 6:

- for demining game:
  - players(array, send the finally rank of players, for players[i]:
    - username
    - rank
- for undercover game:
  - players(array, send the players' status and key word, for players[i]):
    - username
    - keyword
    - undercover (1 for is undercover, 0 for is not undercover)
  - result (1 for friends win, 2 for undercover win)



##### action 7: for different game might have different value

- for who is undercover game:
  - keyword




##### action 8: 

- for undercover game: to be sent after finishing speech process
  - leftTime
  - playerNum
  - aliveNum
  - alive (0 for not alive, 1 for alive)
  - messages (array, to tell the message sent by the alive players), for messages[i]
    - username
    - message
    - canVoted (0 for cannot be voted, 1 for can)




##### action 9:

- for undercover game:
  - voteInfo (array, to tell the voted information), for voteInfo[i]
    - votePlayer
    - votedPlayer
  - leftTime



##### action 10: 

- for undercover game (send when user revisit):
  - gameProcess (0 for speech process, 1 for voting process)
- (for gameProcess = 0)
  - baseInfo (array, about the basic information of the current process) ,for baseInfo[i]
    - username
    - alive (1 for alive, 0 for not alive)
- (for gameProcess = 1)
  - baseInfo (array, about the basic information of the current process) ,for baseInfo[i]
    - username
    - alive
    - message (the user's speech in this turn, refer to vote)
  - userVoted (array, about the user be voted in this turn),for userVoted[i]
    - votedIndex
  - userVote (array, about the user can vote in this turn), for userVote[i]
    - voteIndex




##### action 11:

- for all game:
  - players (an array, to send about the score and delta score of the players), for players[i]
    - username
    - point
    - deltaPoint

##### action 12:

- for all game:
  - username1
  - username2
  - result (0:succeeded 1:already is friend 2:failed)



##### Browser to Server

| action | function                         | part                    |
| ------ | -------------------------------- | ----------------------- |
| 1      | register or log in               | username                |
| 2      | send message                     | message                 |
| 3      | click in demining game           | clickX,clickY,clickType |
| 4      | play request                     | type                    |
| 5      | chat in game                     | message                 |
| 6      | send message for undercover game | message                 |
| 7      | send vote info for undercover    | vote                    |
| 8      | add friend                       | username1, username2    |

