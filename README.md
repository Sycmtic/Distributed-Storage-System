# Distributed-Storage-System

## Preparation:

Register a queue name as your username on hornetq-jms.xml for notification service.

For example:

   \<queue name="mmmmel"\>
      \<entry name="mmmmel" /\>
   \</queue\>

## Running:
run with jar files:<br />
1. run notification HQ server:<br />
open console, run command "java -jar NotificationHQServer.jar"<br />
2. run all five servers on five empty ports<br />
open 1st console, run command "java -jar ServerNode.jar 8081 8082 8083 8084 8085"<br />
open 2nd console, run command "java -jar ServerNode.jar 8082 8081 8083 8084 8085"<br />
open 3rd console, run command "java -jar ServerNode.jar 8083 8081 8082 8084 8085"<br />
open 4th console, run command "java -jar ServerNode.jar 8084 8081 8082 8083 8085"<br />
open 5th console, run command "java -jar ServerNode.jar 8085 8081 8082 8083 8084"<br />
*Note: ServerNode.jar takes a list of port number as argument, first one is the port number for current server, others are port number for peer servers. So please make sure the list of ports are same among all servers with only the first element of the list is different.<br />
3. Run client and make it connect to one of servers<br />
open another console, run command "java -jar Client.jar localhost 8081"<br />
4. login or create a new user<br />
on the client console, run command "login" or "signup"<br />
5. enter the username you want to login/signup<br />
*Note: due to the limit of the project, you have to resigter the username in hornetq-jms.xml file first before you create the user with that username. We provide some default username for you to use with jar file: "mmmmel", "yongle", "muhan"<br />
6. follow the instruction on the terminal and enjoy this tool<br />
<br />
run with java files or IDE:<br />
1. resigter the username you want to signup in hornetq-jms.xml (see Preparation)<br />
2. same step as "run with jar files"<br />
