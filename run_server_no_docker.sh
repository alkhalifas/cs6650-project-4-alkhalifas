#!/bin/bash

cd src

rm -r server/*.class utils/*.class main/ServerMain.class
javac server/*.java utils/*.java main/ServerMain.java

# Run chmod +x run_server_no_docker.sh if needed
java -cp . main.ServerMain
