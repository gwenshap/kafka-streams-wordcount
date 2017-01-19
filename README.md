This example counts words from 'wordcount-input' topic, excluding the word "the", and writes the counts to 'wordcount-output' topic. It is based on Confluent's wordcount example, with very minor changes. You can find the original here:
https://github.com/confluentinc/examples/blob/3.1.x/kafka-streams/src/main/java/io/confluent/examples/streams/WordCountLambdaExample.java

To run this example:

0. Build the project with `mvn package`, this will generate an uber-jar with the streams app and all its dependencies.
1. Create a wordcount-input topic:

    `bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic wordcount-input --partitions 1 --replication-factor 1`

2. Produce some text to the topic. Don't forget to repeat words (so we can count higher than 1) and to use the word "the", so we can filter it.

   `bin/kafka-console-producer.sh --broker-list localhost:9092 --topic wordcount-input`

3. Run the app:

    `java -cp target/uber-kafka-streams-wordcount-1.0-SNAPSHOT.jar com.shapira.examples.streams.wordcount.WordCountExample`

4. Take a look at the results:

    `bin/kafka-console-consumer.sh --topic wordcount-output --from-beginning --bootstrap-server localhost:9092  --property print.key=true`

If you want to reset state and re-run the application (maybe with some changes?) on existing input topic, you can:

1. Reset internal topics (used for shuffle and state-stores):

    `bin/kafka-streams-application-reset.sh --application-id wordcount --bootstrap-servers localhost:9092 --input-topic wordcount-input`

2. (optional) Delete the output topic:

    `bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic wordcount-output`





