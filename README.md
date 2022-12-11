# RMI-Chat-Application

This project develops simple console chat application by utilizing java Remote Method Invocation(RMI)

A single ```RMIServer``` instance should be running. As many as ```RMIClient``` instance could be run and connect to server.

## Features

-   Users have two status states, either available or busy
-   Chat room lobby where all connected users are shown
-   Lobby shows whether a user is available or busy
-   A user may peer up with another available user and start texting. Peered up users will be shown as busy and others will not be able to send them a message.
-   A peered user may terminate ongoing chat and go back to lobby.
-   When a user joins, peers up, terminates a chat, disconnects from the chat application, all other available users in the lobby will be notified with updated status of users.

## In-App Instructions

You will have four options to choose throughout the program.
<br></br>

(1) Now, you are at the home page. You may see all users whether they are busy or available for chatting.
You will also be notified for newcomers and people who leave the chat if you stay here.
<br></br>
(2) You may start a chat with those who are not busy by prompting command of ```$username```. You will not be allowed to start a chat with busy ones. Please select a name from available users list.
<br></br>
(3) Whether you are in the middle of a chat or staying in the home page, you may leave the chat room
for good by prompting command of ```$disconnect```.
<br></br>
(4) You may terminate chatting with your peer and return back to home page any time by prompting command of ```$return"```
<br></br>
NOTE: Please do not use dollar sign($) for messaging since it is a reserve character for commands.



