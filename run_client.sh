#!/bin/bash

# TCP CLIENT

echo "----------------------------"
echo ",--------. ,-----.,------."
echo "'--.  .--''  .--./|  .--. '"
echo "   |  |   |  |    |  '--' |"
echo "   |  |   '  '--'\|  | --'"
echo "   '--'    '-----''--'"
echo "----------------------------"
echo "Running TCP Client..."

java client.ClientApp localhost 1234 tcp

# UDP CLIENT

echo "----------------------------"
echo ",--. ,--.,------.  ,------.  "
echo "|  | |  ||  .-.  \ |  .--. ' "
echo "|  | |  ||  |  \  :|  '--' | "
echo "'  '-'  '|  '--'  /|  | --'  "
echo "''-----' '-------' ''--'     "
echo "----------------------------"
echo "Running UDP Client..."

java client.ClientApp localhost 4321 udp
