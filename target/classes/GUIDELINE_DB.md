# Guideline to the database

- The format of the tables in the database Gameplatform.db.
- The ***item*** means the primary key of a table in the database. 
- Use sqlite3 database and java connection between the database.

### Player

- transformed from the players entity

| name       | type        | description                              |
| :--------- | :---------- | :--------------------------------------- |
| ***p_id*** | INT         | player's id number (auto increment according to the order of register) |
| p_name     | VARCHAR(15) | player's name                            |
| p_key      | VARCHAR(12) | player's password                        |
| p_point    | INT         | player's point                           |



### Game Process

- transformed from the game processes entity

| name        | type     | description                              |
| ----------- | -------- | ---------------------------------------- |
| ***gp_id*** | INT      | one certain game process' id number (auto increment) |
| gp_type     | INT      | game type of the game process            |
| gp_begin_t  | DATETIME | time when the game process began         |
| gp_end_t    | DATETIME | time when the game process ended         |



### Part

- transformed from the participating relation between player and game process entity
- The following "associated" means *this* player participated *this* game process. 

| name        | type | description                              |
| ----------- | ---- | ---------------------------------------- |
| ***pa_id*** | INT  | order in the part list (auto increment)  |
| p_id        | INT  | associated player's id number            |
| gp_id       | INT  | associated game process' id number       |
| pa_res      | INT  | *this* player's point change after *this* game process |



### Friend

- transformed from the friend relation between two players
- The following "associated" means two players have friend relationship. 

| name       | type | description                              |
| ---------- | ---- | ---------------------------------------- |
| ***f_id*** | INT  | order in the friend list (auto increment) |
| p_id1      | INT  | the associated first player              |
| p_id2      | INT  | the associated second player             |

