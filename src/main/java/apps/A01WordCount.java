package apps;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

public class A01WordCount {
    private static final Logger log = LoggerFactory.getLogger(A01WordCount.class);

    public static void main(String[] args) {
        Properties config = new Properties();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG, A01WordCount.class.getName().toLowerCase());
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
                Optional.ofNullable(System.getenv("BOOTSTRAP_SERVERS_CONFIG")).orElse("localhost:9092")
        );
        config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);

        //config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);

        KStreamBuilder builder = new KStreamBuilder();
        KTable<String, Long> countTable = builder.stream(Serdes.String(), Serdes.String(), "sentences")
                .filter((k, v) -> v != null && v.length() > 0)
                .mapValues(v -> v.toLowerCase())
                .flatMapValues(v -> Arrays.asList(v.split(" ")))
                .selectKey((k, v) -> v)
                .groupByKey(Serdes.String(), Serdes.String())
                .count("Count");

        countTable
                .toStream()
                .peek((k, v) -> log.info("{} : {}", k, v))
                .to(Serdes.String(), Serdes.Long(), "sentences-a01wordcount");

        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
            // here you should examine the throwable/exception and perform an appropriate action!
            log.error("Uncaught exception", throwable);
        });
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                streams.close();
                log.info("Stream stopped");
            } catch (Exception exc) {
                log.error("Got exception while executing shutdown hook: ", exc);
            }
        }));
    }
}
