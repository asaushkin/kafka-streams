
mvn clean package dependency:copy-dependencies
cp target/*.jar docker/kafka-connect/

docker-compose up -d --build

