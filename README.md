# Distributed-Storage-System

## Preparation:

Register a queue name as your username on hornetq-jms.xml for notification service.

For example:

   \<queue name="mmmmel"\>
   
      \<entry name="mmmmel" /\>
      
   \</queue\>

Function:

client can check what file he/she owns

client can create a file

client can change a file (let's just change title for now)

client can share a file with another client

change of a file should be notified with all related clients
