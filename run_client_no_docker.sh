#!/bin/bash

echo " "
echo " "
echo " "
echo "##########################################"
echo "  ____            _           _     _  _   "
echo " |  _ \ _ __ ___ (_) ___  ___| |_  | || |  "
echo " | |_) | '__/ _ \| |/ _ \/ __| __| | || |_ "
echo " |  __/| | | (_) | |  __/ (__| |_  |__   _|"
echo " |_|   |_|  \___// |\___|\___|\__|    |_|  "
echo "               |__/                        "
echo "##########################################"
echo "Instructions: "
echo "> Add a value - PUT:KEY:VALUE"
echo "> Get a value - GET:KEY"
echo "> Delete a value - DELETE:KEY"
echo "> Quit the application - quit"
echo "##########################################"


cd src
rm -r client/*.class utils/*.class main/ClientMain.class
javac client/*.java utils/*.java main/ClientMain.java


java -cp . main.ClientMain localhost 1100

echo ""
echo ""
echo ""
echo "############################################################"
echo "Successfully exited. Thanks for using my platform! :)"
echo "############################################################"
echo ""
echo ""
echo ""

