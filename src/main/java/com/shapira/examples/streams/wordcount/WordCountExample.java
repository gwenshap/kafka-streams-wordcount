package com.shapira.examples.streams.wordcount;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

public class WordCountExample {

    private static final String INPUT_TOPIC = "wordcount-input";

    private static final String OUTPUT_TOPIC = "wordcount-output";

    private static final Pattern SPLIT_WORDS_PATTERN = Pattern.compile("\\W+");

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KStreamBuilder builder = new KStreamBuilder();

        KStream<String, String> source = builder.stream(INPUT_TOPIC);

        KStream counts = source.flatMapValues(value -> Arrays.asList(SPLIT_WORDS_PATTERN.split(value.toLowerCase())))
                .map((key, value) -> new KeyValue<>(value, value))
                .filter((key, value) -> (!value.equals("the")))
                .groupByKey()
                .count("WordCountStore")
                .mapValues(value -> Long.toString(value)).toStream();
        counts.to(OUTPUT_TOPIC);

        KafkaStreams streams = new KafkaStreams(builder, props);

        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
