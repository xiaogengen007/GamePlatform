# GuideLine to This Program

### the communication standard

- protocol: websocket


- text format: JSON

##### Server to Browser

| action | function               | part                                  |
| ------ | ---------------------- | ------------------------------------- |
| 1      | game status            | start,playerNum                       |
| 2      | send message           | message                               |
| 3      | state in game          | finished,leftTime,finishNum,playerNum |
| 4      | finish one turn(Demin) | state,leftTime                        |
| 5      | chat in game           | message                               |

##### Browser to Server

| action | function               | part                    |
| ------ | ---------------------- | ----------------------- |
| 1      | register or log in     | username                |
| 2      | send message           | message                 |
| 3      | click in demining game | clickX,clickY,clickType |
| 4      | play request           | type                    |
| 5      | chat in game           | message                 |

