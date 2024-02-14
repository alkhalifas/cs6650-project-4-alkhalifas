#!/bin/bash

# Define variables
PROJECT_NETWORK="project-1-network"
SERVER_IMAGE="project-1-server-image"
SERVER_CONTAINER="project-1-server-container"
CLIENT_IMAGE="project-1-client-image"

# Create a custom Docker network
docker network create $PROJECT_NETWORK || true

# Build server image
docker build -t $SERVER_IMAGE --target server-build .

# Run server container
docker run -d --network $PROJECT_NETWORK --name $SERVER_CONTAINER -p 1111:1111/tcp -p 5555:5555/udp $SERVER_IMAGE
