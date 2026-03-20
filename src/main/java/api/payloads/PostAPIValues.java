package api.payloads;

import api.models.PostAPIPojo;

/** Concrete payload class for creating posts with default constructor values and abstract class handling */
public class PostAPIValues extends BasePayload<PostAPIPojo> {

    public PostAPIValues() {
        body = new PostAPIPojo();
        body.setUserId(5);             // default values
        body.setTitle("Default Title");
        body.setBody("Default Body");
    }

    /** Returns the payload class type for BasePayload operations */
    @Override
    protected Class<PostAPIPojo> getPayloadClass() {
        return PostAPIPojo.class;
    }
}