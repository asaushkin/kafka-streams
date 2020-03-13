FROM confluentinc/cp-server-connect:5.4.1

RUN confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:0.3.1

COPY ./kafka-streams-*.jar /usr/share/java/kafka-connect-elasticsearch/
