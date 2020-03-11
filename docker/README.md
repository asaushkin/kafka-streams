# How to add a new datagen source connector

1. Create a new datagen file and put it inside `docker/kafka-connect/avro/something_in_avro.avro`

   For example see `docker/kafka-connect/avro/impressions.avro`
   
2. Create connector. See example - `docker/debug/connectors/impressions.json`

3. Topic with that name will be created automatically with the connector 
   name

4. Rebuild image, run a command `docker-compose up --build`

## How to add ksql stream

Connect to the ksql server 

```shell script
ksql http://ksql-server:8088
```

Execute code like this:

```sql
CREATE STREAM users (registertime bigint, userid varchar, regionid varchar, 
    gender varchar) 
with (kafka_topic='users',value_format='JSON');

CREATE TABLE users_extended (
    registertime BIGINT,
    gender VARCHAR,
    regionid VARCHAR,
    userid VARCHAR,
    interests ARRAY<STRING>,
    contactInfo MAP<STRING, STRING>)
WITH (
    kafka_topic='users_extended',
    value_format='JSON',
    key = 'userid');

CREATE STREAM pageviews (
    viewtime bigint,
    userid varchar,
    pageid varchar)
WITH (
    kafka_topic='pageviews',
    value_format='JSON');
```

Join of two streams

```sql
select * from pageviews as p 
    join users as u within 5 minutes 
        on u.userid = p.userid;
```

Join stream and table

```sql
select * from pageviews as p join users_extended as u on u.userid = p.userid;
```

For full examples see here: https://docs.confluent.io/current/ksql/docs/tutorials/generate-custom-test-data.html

## How to view a topic contents

```shell script
kafkacat -b kafka:9092 -t users \
  -f 'Topic %t[%p], offset: %o, key: %k, payload: %S bytes: %s\n'
``` 
