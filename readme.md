### Single Server, Key-Value Store (TCP and UDP)

Requirements:
- [ ] Docker is optional, but not required
- [X] You must also use two distinct L4 communication protocols
- [X] Your implementation may be written in Java
- [X] Your source code should be well-factored and well-commented.
- [X] The client must take the following command line arguments hostname/IP and Port and protocol
- [X] The client should be robust to server failure by using a timeout mechanism
- [ ] if it does not receive a response to a particular request, you should note it in a client log and send the remaining requests
- [X] You will have to design a simple protocol to communicate packet contents
- [X] Three request types along with data passed along as part of the requests
- [X] The client must be robust to malformed or unrequested datagram packets
- [X] If it receives such a datagram packet, it should report it in a human-readable way
- [X] Every line the client prints to the client log should be time-stamped with the current system time
- [X] You may format the time any way you like as long as your output maintains millisecond precision
- [X] You must have two instances of your client (or two separate clients)
- [X] The server must take the following command line arguments: port numbers for TCP and UDP
- [X] The server should run forever (until forcibly killed by an external signal, such as a Control-C
- [X] The server must display the requests received, and its responses, both in a human readable
- [X] that is, it must explicitly print to the server log that it received a query from a particular InetAddress and port number
- [ ] The server must be robust to malformed datagram packets
- [ ] If it receives a malformed datagram packet, it should report it in a human-readable way (length + address:port)
- [X] Every line the server prints to standard output or standard error must be time-stamped with the current system time (i.e., System.currentTimeMillis()).
- [X] You may format the time any way you like as long as your output maintains millisecond precision
- [X] You must have two instances of your server (or two separate servers)
- [ ] You should use your client to pre-populate the Key-Value store with data and a set of keys.
- [ ] Once the key-value store is populated, your client must do at least five of each operation: 5 PUTs, 5 GETs, 5 DELETEs.
- [ ] “Assignment overview” (1 paragraph, up to about 250 words) explaining what you understand to be the purpose and scope of the assignment
- [ ] “technical impression” (1–2 paragraphs, about 200–500 words) describing your experiences while carrying out the assignment.
- [ ] Provide a use case (application) 3 where you would apply this in practice.
- [ ] Attach screenshots of your testing done on your local environment.
- [ ] 

### Todo:
- [X] Create directories and structure
- [X] Create a Hash Map could be used for setting up Key value stores.
- [X] Hashmap persists in memory
- [X] Client TCP: Create functional client that can make calls via TCP
- [X] Client UDP: Create functional client that can make calls via UDP
- [X] Server TCP: Create functional server that can make calls via TCP
- [X] Server UDP: Create functional server that can make calls via UDP
- [X] Be configurable such that you can dictate that client and server communicate using TCP/UDP
- [X] Comment your code and appropriately split the project into multiple functions and/or classes
- [X] Client to take the hostname or IP address of the server (it must accept either)
- [X] Client to take the port number of the server
- [X] The client should be robust to server failure by using a timeout mechanism to deal with an unresponsive server
- [ ] If it does not receive a response to a particular request, you should note it in a client log and send the remaining requests
- [X] You will have to design a simple protocol to communicate packet contents for the three request types
- [ ] The client must be robust to malformed or unrequested datagram packets
- [X] Every line the client prints to the client log should be time-stamped with the current system time
- [X] The server must take the following command line arguments: address, port
- [X] The server should run forever
- [X] The server must display the requests received, and its responses
- [ ] explicitly print to the server log that it received a query from a particular InetAddress and port number for a specific word
- [ ] should report it in a human-readable way
- [X] You must have two instances of your server (or two separate servers)
- [X] You should use your client to pre-populate the Key-Value store with data and a set of keys.
- [X] Once the key-value store is populated, your client must do at least five of each operation: 5 PUTs, 5 GETs, 5 DELETEs.
- [ ] Part of your completed assignment submission should be an executive summary containing an “Assignment overview”
- [ ] Implement an encoder/decoder
- [ ] Check requestID for malformed data
- [ ] Containerization is not needed. Java Files along with README should be good to go.
- [ ] Please spend some time to make a proper `ReadME` markdown file, explaining all the steps necessary to execute your source code.
- [X] Do not hardcode IP address or port numbers, try to collect these configurable information from config file/env variables/cmd input args. 
- [ ] Attach screenshots of your testing done on your local environment.]


### Instructions:

Compile the Java code:

    javac server/*.java client/*.java

Run the Server:

    java server.ServerApp 1111 5555

Run the Client:

    java client.ClientApp localhost 1111 tcp
    java client.ClientApp localhost 5555 udp

### Questions for TA:

1. Am I passing the 'hostname' correctly?
2. What is the best way to handle both TCP and UDP?
3. Can I assume that the server cannot 'set' the hostname or IP? When you deploy this, 
it should be default live on a server with an already assigned IP address.
4. How do I handle both TCP and UDP on ClientApp?
5. Do I need Docker? I see conflicting information.