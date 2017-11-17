# GuideLine to This Program

### the communication standard

- protocol: websocket


- text format: JSON

##### Server to Browser

| action | function               | part                                     |
| ------ | ---------------------- | ---------------------------------------- |
| 1      | game status            | start,players                            |
| 2      | send message           | message                                  |
| 3      | state in game          | finished,leftTime,finishNum,playerNum,(preState) |
| 4      | finish one turn(Demin) | state,leftTime                           |
| 5      | chat in game           | message                                  |

more information

- action 1: players(array,store the players info in the room) ; 

  players[i]: 

  - username

- action 3: preState(array, only to the players finished this turn); 

  preState[i]:

  - username
  - clickType(1 for click left button, 0 for click right button)
  - clickX(0~7)
  - clickY(0~7)

##### Browser to Server

| action | function               | part                    |
| ------ | ---------------------- | ----------------------- |
| 1      | register or log in     | username                |
| 2      | send message           | message                 |
| 3      | click in demining game | clickX,clickY,clickType |
| 4      | play request           | type                    |
| 5      | chat in game           | message                 |

