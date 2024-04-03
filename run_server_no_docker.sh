#!/bin/bash

cd src

rm -r coordinator/*.class server/*.class utils/*.class main/ServerMain.class
javac coordinator/*.java server/*.java utils/*.java main/ServerMain.java

# Run chmod +x run_server_no_docker.sh if needed
java -cp . main.ServerMain
