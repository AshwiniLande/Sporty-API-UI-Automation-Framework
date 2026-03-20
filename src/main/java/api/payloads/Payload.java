package api.payloads;

import java.util.Map;

/** Base interface for all API payloads. Provides contract for getting payload body and overriding default values.*/
public interface Payload {
    /** Return actual POJO body */
    Object getBody();

    /** Merge override values */
    void overrideValues(Map<String, Object> overrideValues);
}