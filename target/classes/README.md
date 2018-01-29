# GamePlatform
A game platform software done for the big job of software engineering.

## 1. eclipse encoding setting

Please set the eclipse encoding as follows

- Window->Preferences->General ->Content Type->Text->JSP 最下面设置为UTF-8
- Window->Preferences->General->Workspace   面板Text file encoding 选择UTF-8
- Window->Preferences->Web->JSP Files 面板选择 ISO 10646/Unicode(UTF-8)

## 2. solve the question Chrome can't load the new page

you might need to clean the chrome's cookies:

- 更多工具->清除浏览数据->Cookie及其他网站数据（勾选上）

sometimes the above might not be useful, you may do more to clean the cache of js, which can refer to the website: http://www.voidcn.com/article/p-yodistev-bcy.html

## 3. set some useful  parameters 

to test the program more effectively, you might need to set some parameters, here are the method to set them:

- set the maximum time(second) in one turn, in the class **DeminGame.java**, in its construction function **DeminGame()** , change the command **this.maxTurnTime = 1;** its right number is the maximum time in one turn;


## 4. init your database profile

Before using this program for sever and database, you need to create a folder **C:/resource/database/** to save for your database.

note that the database has changed format in the 2017.12.22 itachi4869(Li Boyao)'s commit (https://github.com/chuzhumin98/GamePlatform/commit/b493334c76e172709dd0ce75491b5de7690ea362 to see the specific changes), please delete the old database and let it automatically rebuild.