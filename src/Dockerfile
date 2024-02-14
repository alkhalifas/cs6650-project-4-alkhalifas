# Base stage for both client and server
FROM openjdk:11-jdk AS base
WORKDIR /app

# Build stage for the client
FROM base AS client-build
COPY ./client /app/client
RUN javac client/*.java

# Build stage for the server
FROM base AS server-build
COPY ./server /app/server
RUN javac server/*.java
CMD ["java", "server.ServerApp", "1111", "5555"]
