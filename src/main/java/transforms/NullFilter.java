package transforms;

import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NullFilter<R extends ConnectRecord<R>> implements Transformation<R>
{
    static final Logger log = LoggerFactory.getLogger(NullFilter.class);

    public R apply(final R r) {
        if (r.value() == null) {
            return null;
        }
        return r;
    }

    public ConfigDef config() {
        return new ConfigDef();
    }

    public void close() {
    }

    public void configure(final Map<String, ?> map) {
    }
}
