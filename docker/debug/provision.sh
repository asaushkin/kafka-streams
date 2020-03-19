#!/bin/bash

replication_factor=1
partitions=1

bootstrap_server=kafka:29092
kafka_connect=kafka-connect:8083

#  wait up to 60 seconds for kafka-connect
(while [[ $count -lt 60 && -z `curl -sf ${kafka_connect}/connectors` ]]; do ((count=count+1)) ; echo "Waiting for kafka-connect" ; sleep 2; done && [[ $count -lt 60 ]])
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

for i in /ksql/*.ksql; do
  cat $i | ksql http://ksql-server:8088
done

kafka-topics --bootstrap-server $bootstrap_server --create \
  --topic colours-input --partitions 1 --replication-factor 1

kafka-topics --bootstrap-server $bootstrap_server --create \
  --topic colours-intermediate --partitions 1 --replication-factor 1 \
  --config cleanup.policy=compact
#  --config min.cleanable.dirty.ratio=0.005 \
#  --config segment.ms=1000

kafka-topics --bootstrap-server $bootstrap_server --create \
  --topic colours-output --partitions 1 --replication-factor 1 \
  --config cleanup.policy=compact
#  --config min.cleanable.dirty.ratio=0.005 \
#  --config segment.ms=1000

tail -f /dev/null
