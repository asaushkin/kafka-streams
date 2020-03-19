package apps;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

public class B01FavouriteColour {
    private static final Logger log = LoggerFactory.getLogger(B01FavouriteColour.class);

    public static void main(String[] args) {
        Properties config = new Properties();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG, B01FavouriteColour.class.getName().toLowerCase());
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
                Optional.ofNullable(System.getenv("BOOTSTRAP_SERVERS_CONFIG")).orElse("localhost:9092")
        );
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        //config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
        config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

        //config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);

        KStreamBuilder builder = new KStreamBuilder();

        builder.stream(Serdes.String(), Serdes.String(), "colours-input")
                .selectKey((k, v) -> v.split(",")[0])
                .mapValues(v -> v.split(",")[1])
                .to(Serdes.String(), Serdes.String(), "colours-intermediate");

        builder.table(Serdes.String(), Serdes.String(), "colours-intermediate")
                //.toStream()
                //.selectKey((k, v) -> v)
                .groupBy((user, colour) -> new KeyValue<>(colour, colour))
                .count("Count")
                //.toStream()
                //.peek((k, v) -> log.info("{} : {}", k, v))
                .to(Serdes.String(), Serdes.Long(), "colours-output");

        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
            // here you should examine the throwable/exception and perform an appropriate action!
            log.error("Uncaught exception", throwable);
        });
        streams.cleanUp();
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
