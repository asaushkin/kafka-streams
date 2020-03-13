
mvn clean package
cp target/*.jar docker/kafka-connect/

docker-compose up -d --build

