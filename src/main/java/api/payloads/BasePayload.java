package api.payloads;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/** Abstract base class providing default handling for API payload body and overrides */
public abstract class BasePayload<T> implements Payload {

    private static final Logger logger = LogManager.getLogger(BasePayload.class);

    protected T body;
    protected abstract Class<T> getPayloadClass();

    /** Overrides payload fields with provided map values */
    @Override
    public void overrideValues(Map<String, Object> overrideValues) {
        if (body == null) {
            logger.warn("Cannot override values: payload body is null!");
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode defaultNode = mapper.valueToTree(body);
            ObjectNode modifiedNode = mapper.valueToTree(overrideValues);
            defaultNode.setAll(modifiedNode);
            body = mapper.convertValue(defaultNode, getPayloadClass());
        } catch (Exception e) {
            logger.error("Failed to override payload values", e);
            throw new RuntimeException("Payload override failed", e);
        }
    }

    /** Returns payload body, logs warning if null */
    @Override
    public T getBody() {
        if (body == null) {
            logger.warn("Payload body is null when getBody() called!");
        }
        return body;
    }
}