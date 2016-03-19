#!/bin/bash

echo "wOOt!"

apt-get update
apt-get install -y redis-server

cp -u /vagrant/redis.conf /etc/redis/redis.conf