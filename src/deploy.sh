#!/bin/bash

# Define variables
PROJECT_NETWORK="project-2-network"
SERVER_IMAGE="project-2-server-image"
SERVER_CONTAINER="project-2-server-container"
CLIENT_IMAGE="project-2-client-image"
RMI_PORT=1099

# Create a custom Docker network
docker network create $PROJECT_NETWORK || true

# Build server image
docker build -t $SERVER_IMAGE --target server-build .

# Run server container
docker run -d --network $PROJECT_NETWORK --name $SERVER_CONTAINER -p $RMI_PORT:$RMI_PORT $SERVER_IMAGE
