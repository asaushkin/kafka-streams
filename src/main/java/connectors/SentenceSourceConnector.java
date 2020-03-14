package connectors;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentenceSourceConnector extends SourceConnector {

    @Override
    public void start(Map<String, String> props) {
        nouns = props.get(NOUNS);
        verbs = props.get(VERBS);
        modifiers = props.get(MODIFIERS);
        batchSize = props.get(BATCH_SIZE);
        sleepMs = props.get(SLEEP_MS);
        topic = props.get(TOPIC);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return SentenceSourceTask.class;
    }

    static final String NOUNS = "nouns";
    static final String VERBS = "verbs";
    static final String MODIFIERS = "modifiers";
    static final String BATCH_SIZE = "batch-size";
    static final String SLEEP_MS = "sleep-ms";
    static final String TOPIC = "topic";

    String nouns;
    String verbs;
    String modifiers;
    String batchSize;
    String sleepMs;
    String topic;

    static final String VERSION = "0.1.0";

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<>();
        Map<String, String> config = new HashMap<>();

        config.put(NOUNS, nouns);
        config.put(VERBS, verbs);
        config.put(MODIFIERS, modifiers);
        config.put(BATCH_SIZE, batchSize);
        config.put(SLEEP_MS, sleepMs);
        config.put(TOPIC, topic);

        configs.add(config);
        return configs;
    }

    @Override
    public void stop() {
    }

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(NOUNS, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Nouns for the senteces")
            .define(VERBS, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Verbs for the sentence")
            .define(MODIFIERS, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Modifiers for the sentence")
            .define(BATCH_SIZE, ConfigDef.Type.INT, ConfigDef.Importance.HIGH, "Sentences batch size")
            .define(SLEEP_MS, ConfigDef.Type.LONG, ConfigDef.Importance.HIGH, "Sleep betwen batches")
            .define(TOPIC, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Destination topic")
            ;

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public String version() {
        return VERSION;
    }
}
