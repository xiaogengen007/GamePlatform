# GuideLine to This Program

### the communication standard

- protocol: websocket


- text format: JSON

##### Server to Browser

| action | function           | part                              |
| ------ | ------------------ | --------------------------------- |
| 1      | game status        | start,players,(...)               |
| 2      | send message       | message                           |
| 3      | state in game      | finished,leftTime,playerNum,(...) |
| 4      | finish one turn    |                                   |
| 5      | chat in game       | message                           |
| 6      | game result        | players                           |
| 7      | other game state   |                                   |
| 8      | finish one process |                                   |

more information

##### action 1: 

- for all game:

  - players(array,store the players info in the room) ,for players[i]
    - username

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

  ​

##### action 6:

- for all game:
  - players(array, send the finally rank of players, for players[i]:
    - username
    - rank

​

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

