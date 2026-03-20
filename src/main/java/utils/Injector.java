package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** Utility class to auto-inject runtime values from RuntimeStore into maps or resource paths */
public final class Injector {

    private Injector() {} // private constructor

    /** Injects RuntimeStore values into the provided map if the value is a RuntimeStore key */
    public static Map<String,Object> autoInject(Map<String,Object> overrideValues) {
        Map<String,Object> output = new HashMap<>();

        for(Map.Entry<String,Object> entry : overrideValues.entrySet()) {
            Object value = entry.getValue();
            if(value instanceof String && RuntimeStore.contains((String)value)) {
                output.put(entry.getKey(), RuntimeStore.get((String)value));
            } else {
                output.put(entry.getKey(), value);
            }
        }

        return output;
    }

    /** Replaces placeholders in resourceLink with corresponding RuntimeStore values */
    public static String pathInject(String resourceLink) {
        if (resourceLink == null || resourceLink.isEmpty()) {
            throw new IllegalArgumentException("Resource path cannot be null or empty");
        }

        Set<String> keys = RuntimeStore.getKeys();
        if (keys != null) {
            for (String key : keys) {
                Object value = RuntimeStore.get(key);
                if (value != null) {
                    resourceLink = resourceLink.replace("{{" + key + "}}", value.toString());
                }
            }
        }

        return resourceLink;
    }
}
