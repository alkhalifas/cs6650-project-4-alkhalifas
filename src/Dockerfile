# Base stage for both client and server
FROM openjdk:11-jdk AS base
WORKDIR /app

# Copy and compile common package
COPY ./common /app/common
RUN javac common/*.java

# Build stage for the client
FROM base AS client-build
COPY ./client /app/client
# Ensure common package classes are compiled and available for the client
RUN javac -cp .:common/* client/*.java

# Build stage for the server
FROM base AS server-build
COPY ./server /app/server
# Ensure common package classes are compiled and available for the server
RUN javac -cp .:common/* server/*.java
CMD ["java", "server.ServerApp", "1099"]
