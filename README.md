# Kafka Streams Word Count (using docker)

This example counts words from 'wordcount-input' topic, excluding the word "the", and writes the counts to 'wordcount-output' topic. It is based on Confluent's wordcount example, with very minor changes. You can find the original here:
https://github.com/confluentinc/examples/blob/3.1.x/kafka-streams/src/main/java/io/confluent/examples/streams/WordCountLambdaExample.java

To run this example:

## start zookeeper

    docker run -d --name zookeeper jplock/zookeeper

## start kafka

    docker run -d --name kafka --link zookeeper:zookeeper ches/kafka

## Create a wordcount-input topic

    docker run --rm --link zookeeper:zk ches/kafka kafka-topics.sh --zookeeper zk:2181 --create --topic wordcount-input --partitions 1 --replication-factor 1

## Produce some text to the topic

Don't forget to repeat words (so we can count higher than 1) and to use the word "the", so we can filter it.

    docker run --rm -i --link kafka:kafka ches/kafka kafka-console-producer.sh --broker-list kafka:9092 --topic wordcount-input

## Build the app

From the root project folder, run:

    mvn package
    
It generates an uber-jar with the streams app and all its dependencies in the target folder.

## Run the app

From the root project folder, run:

    docker run -it --link kafka:kafka -v ${PWD}:/tmp/kswc --rm openjdk:8 java -jar /tmp/kswc/target/uber-kafka-streams-wordcount-1.0-SNAPSHOT.jar kafka

## Take a look at the results:

    docker run --rm --link kafka:kafka ches/kafka kafka-console-consumer.sh --topic wordcount-output --from-beginning --bootstrap-server kafka:9092  --property print.key=true

## Re-run...

If you want to reset state and re-run the application (maybe with some changes?) on existing input topic, you can:

### Reset internal topics (used for shuffle and state-stores):

    docker run --rm --link kafka:kafka --link zookeeper:zk ches/kafka kafka-streams-application-reset.sh --application-id wordcount --zookeeper zk:2181 --bootstrap-servers kafka:9092 --input-topic wordcount-input

### (optional) Delete the output topic:

    docker run --rm --link zookeeper:zk ches/kafka kafka-topics.sh --zookeeper zk:2181 --delete --topic wordcount-output


