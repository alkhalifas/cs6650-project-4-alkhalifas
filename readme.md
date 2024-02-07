### Single Server, Key-Value Store (TCP and UDP)

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
- [ ] The server must take the following command line arguments: address, port
- [X] The server should run forever
- [X] The server must display the requests received, and its responses
- [ ] explicitly print to the server log that it received a query from a particular InetAddress and port number for a specific word
- [ ] should report it in a human-readable way
- [X] You must have two instances of your server (or two separate servers)
- [ ] You should use your client to pre-populate the Key-Value store with data and a set of keys.
- [ ] Once the key-value store is populated, your client must do at least five of each operation: 5 PUTs, 5 GETs, 5 DELETEs.
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