package api.enumtypes;

/** Enum-based manager holding all API resource paths in a central, immutable way */
public class ResouceLinks {

    public enum Resource {
        /** Enum holding all resource paths for API endpoints */
        POSTS("/posts"),REMOVE("/posts/{{responseId}}")
        ,GETPOST("/posts/{{responseId}}"), GETALLPOSTS("/posts");

        private final String resourceName;

        Resource(String resourceName) {
            this.resourceName = resourceName;
        }

        /** Returns the resource path string */
        public String getResourceName() {
            return resourceName;
        }
    }

}
