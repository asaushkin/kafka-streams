package transforms;

import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.connector.ConnectRecord;

public class NullFilter<R extends ConnectRecord<R>> implements Transformation<R>
{
    public R apply(final R r) {
        return (r.value() == null) ? null : r;
    }

    public ConfigDef config() {
        return new ConfigDef();
    }

    public void close() {
    }

    public void configure(final Map<String, ?> map) {
    }
}
