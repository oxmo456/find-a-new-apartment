#!/bin/sh

PROJECT_NAME=${PWD##*/}

docker-machine inspect $PROJECT_NAME

if [ $? -eq 0 ]; then
    docker-machine stop  $PROJECT_NAME
    docker-machine start $PROJECT_NAME
fi

if [ $? -ne 0 ]; then
    docker-machine create --driver virtualbox $PROJECT_NAME
fi

eval "$(docker-machine env $PROJECT_NAME)"

docker run -d -p 6379:6379  redis

export FANA_REDIS_HOST="$(docker-machine ip $PROJECT_NAME)"

echo 'Yeehaw!!!'


