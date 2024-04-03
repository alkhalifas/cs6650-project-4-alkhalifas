CLIENT_IMAGE='project-2-client-image'
PROJECT_NETWORK='project-2-network'
SERVER_CONTAINER="project-2-server-container"
if [ $# -ne 2 ]
then
  echo "Usage: ./run_client.sh <container-name> <port-number>"
  exit
fi

# run client docker container with cmd args
docker run -it --rm --name "$1" \
 --network $PROJECT_NETWORK $CLIENT_IMAGE \
 java client.ClientApp $SERVER_CONTAINER "$2"