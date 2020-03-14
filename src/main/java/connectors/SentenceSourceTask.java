package connectors;

import generators.SentenceGenerator;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.SplitWords;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static connectors.SentenceSourceConnector.*;

public class SentenceSourceTask extends SourceTask {

    private static final Logger log = LoggerFactory.getLogger(SentenceSourceTask.class);

    @Override
    public String version() {
        return VERSION;
    }

    private SentenceGenerator sentenceGenerator;
    private long sleepMs;
    private int batchSize;
    private String topic;

    @Override
    public void start(Map<String, String> props) {
        sentenceGenerator =
                new SentenceGenerator(
                        SplitWords.toWords(props.get(NOUNS)),
                        SplitWords.toWords(props.get(VERBS)),
                        SplitWords.toWords(props.get(MODIFIERS)));
        sleepMs = Long.parseLong(props.get(SLEEP_MS));
        batchSize = Integer.parseInt(props.get(BATCH_SIZE));
        topic = props.get(TOPIC);

        log.info("Starting sentence generator task");
        log.info("nouns: {}", props.get(NOUNS));
        log.info("verbs: {}", props.get(VERBS));
        log.info("modifiers: {}", props.get(MODIFIERS));
        log.info("sleep-ms: {}", props.get(SLEEP_MS));
        log.info("batch-size: {}", props.get(BATCH_SIZE));
        log.info("topic: {}", props.get(TOPIC));
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        ArrayList<SourceRecord> records = new ArrayList<>();

        for (int i = 0; i < batchSize; i++) {
            Map<String, String> sourcePartition = Collections.singletonMap("sentence", "");
            Map<String, String> sourceOffset = Collections.singletonMap("position", "");

            String sentence = sentenceGenerator.nextSentence();
            records.add(new SourceRecord(sourcePartition, sourceOffset, topic,
                    null,
                    Schema.INT32_SCHEMA, sentence.hashCode(),
                    Schema.STRING_SCHEMA, sentence));
        }
        Thread.sleep(sleepMs);
        return records;
    }

    @Override
    public void stop() {
    }
}
