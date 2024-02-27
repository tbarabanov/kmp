from root of the project run

- docker compose up

create topic `pike` and send a couple of messages

- mvn clean package
- java -jar target/kmp-1.0.0.jar

go to http://localhost:8085/actuator/prometheus

find `spring_kafka_listener` meter

send a couple of messages again

check `spring_kafka_listener` metrics

metrics will not change, no custom tags added