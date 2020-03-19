package utils;

import java.util.List;
import java.util.Map;

public class PrettyPrint {
    @SuppressWarnings("unchecked")
    public static String hash(final Map<String, ?> m, String... offset) {
        StringBuilder retval = new StringBuilder();
        String delta = offset.length == 0 ? "" : offset[0];
        for( Map.Entry<String, ?> e : m.entrySet() ) {
            retval.append(delta).append("[").append(e.getKey()).append("] -> ");
            Object value = e.getValue();
            if( value instanceof Map ) {
                retval.append("(Hash)\n").append(hash((Map<String, Object>) value, delta + "  "));
            } else if( value instanceof List) {
                retval.append("{");
                for( Object element : (List)value ) {
                    retval.append(element).append(", ");
                }
                retval.append("}\n");
            } else {
                retval.append("[").append(value.toString()).append("]\n");
            }
        }
        return retval+"\n";
    }
}
