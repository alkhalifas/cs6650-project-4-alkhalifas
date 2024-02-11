# Compile Code:
javac server/*.java client/*.java

# Start Server:
java server.ServerApp 1234 4321

# first step:
java client.ClientApp localhost 4321 udp

# second
java client.ClientApp localhost 1234 tcp
