### Single Server, Key-Value Store (TCP and UDP)

### Todo:
- [X] Create directories and structure
- [X] Create a Hash Map could be used for setting up Key value stores.
- [X] Hashmap persists in memory
- [X] Client TCP: Create functional client that can make calls via TCP
- [ ] Client UDP: Create functional client that can make calls via UDP
- [X] Server TCP: Create functional server that can make calls via TCP
- [ ] Server UDP: Create functional server that can make calls via UDP
- [ ] Be configurable such that you can dictate that client and server communicate using TCP/UDP
- [ ] Comment your code and appropriately split the project into multiple functions and/or classes
- [ ] Client to take the hostname or IP address of the server (it must accept either)
- [ ] Client to take the port number of the server
- [ ] The client should be robust to server failure by using a timeout mechanism to deal with an unresponsive server
- [ ] If it does not receive a response to a particular request, you should note it in a client log and send the remaining requests
- [ ] You will have to design a simple protocol to communicate packet contents for the three request types
- [ ] The client must be robust to malformed or unrequested datagram packets
- [ ] Every line the client prints to the client log should be time-stamped with the current system time
- [ ] The server must take the following command line arguments: address, port
- [X] The server should run forever
- [ ] The server must display the requests received, and its responses
- [ ] explicitly print to the server log that it received a query from a particular InetAddress and port number for a specific word
- [ ] should report it in a human-readable way
- [ ] You must have two instances of your server (or two separate servers)
- [ ] You should use your client to pre-populate the Key-Value store with data and a set of keys.
- [ ] Once the key-value store is populated, your client must do at least five of each operation: 5 PUTs, 5 GETs, 5 DELETEs.
- [ ] Part of your completed assignment submission should be an executive summary containing an “Assignment overview”
- [ ] Implement an encoder/decoder


### Instructions:

Compile the Java code:

    javac server/*.java client/*.java

Run the Server:

    java server.ServerApp 1111 5555

