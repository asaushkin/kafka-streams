
docker-compose stop kafka-connect
docker-compose rm -f kafka-connect

mvn package
cp target/*.jar docker/kafka-connect/

docker-compose up -d --build kafka-connect

