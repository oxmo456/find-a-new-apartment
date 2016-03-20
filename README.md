# FANA

## Prerequisites

- *SBT 0.13.9*
- *Vagrant 1.8.1*

## Setup

- checkout project `git clone git@github.com:oxmo456/apartment-viewer.git`
- `cd` into the project
- start the *Redis* server `vagrant up`
  - you can test that the server is up with `(printf "PING\r\n"; sleep 1;) | nc localhost 6379`
- start the application `sbt run`

## Helpful information
 
- `(printf "FLUSHALL\r\n"; sleep 1;) | nc localhost 6379` delete all redis local keys/data