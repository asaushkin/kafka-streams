
mvn package
cp target/*.jar docker/kafka-connect/

docker-compose up -d --build kafka-connect

