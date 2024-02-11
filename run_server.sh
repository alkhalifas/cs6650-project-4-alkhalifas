#!/bin/bash

cd src
javac server/*.java client/*.java

# Run chmod +x run_server.sh if needed
java -cp . server.ServerApp 1234 4321
