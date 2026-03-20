package api.enumtypes;

import api.payloads.Payload;
import api.payloads.PostAPIValues;

import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Enum-based factory providing immutable, singleton-like payload instances via Supplier,
 * preventing reflection-based instantiation */

public enum JsonPayload {

    /** Supplier-based payload for creating a post */
    CreatePost(PostAPIValues::new);

    private static final Logger logger = LogManager.getLogger(JsonPayload.class);

    private Supplier<Payload> payloadSupplier;

    JsonPayload(Supplier<Payload> payloadSupplier) {
        this.payloadSupplier = payloadSupplier;
    }

    /** Returns payload instance; logs error if body is null */
    public Payload getPayloadInstance() {
        Payload payload = payloadSupplier.get();
        if (payload == null || payload.getBody() == null) {
            logger.error("Payload body is empty for " + this.name());
        } else {
            logger.info("Payload instance created successfully for " + this.name());
        }
        return payload;
    }
}