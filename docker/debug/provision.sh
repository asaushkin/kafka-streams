#!/bin/bash

replication_factor=1
partitions=1

bootstrap_server=kafka:9092
kafka_connect=connect:8083

#  wait up to 60 seconds for namenode
(while [[ $count -lt 30 && -z `curl -sf ${kafka_connect}/connectors` ]]; do ((count=count+1)) ; echo "Waiting for kafka-connect" ; sleep 2; done && [[ $count -lt 30 ]])
[[ $? -ne 0 ]] && echo "Timeout waiting for kafka-connect, exiting." && exit 1

function create_topic() {
  kafka-topics --bootstrap-server $bootstrap_server --create \
    --topic $1 --partitions $partitions --replication-factor $replication_factor
}

for i in /connectors/*.json; do
  f="${i##*/}"; connector=${f%.[^.]*} ;
  create_topic $connector
  curl -s -X PUT -d @${i} -H "Content-Type: application/json" \
    ${kafka_connect}/connectors/${connector}/config | jq .
done

tail -f /dev/null
