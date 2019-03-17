Miggy's Easy Telnet Chat Server Client:
A very simple to use yet functional chat server using java

to run server:
type "java ChatServer" on command-line screen where ChatServer.class is located and hit <enter>.
notes: client connection information has been improved.

to run client:
type "java ChatClient" on command-line screen where ChatClient.class is located and hit <enter>.
a. if server is on same machine: just hit enter.
b. if server is on different machine: enter the server's ip address.
notes: your default username is GUEST##### where ##### is a random integer. programs were also tested 
to support multiple clients located on different windows machines within the same network. this works
using multi-threading.

to send message from client screen:
type message and hit <enter>.

to change username:
usage: /nick <new username>
sample: /nick MIGGY
notes: all clients are notified when a client changes username. 

to show list of users:
usage: /list
notes: list is updated when a client changes username.

to exit and disconnect from server:
usage: /exit
