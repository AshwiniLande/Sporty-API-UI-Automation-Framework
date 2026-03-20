package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.apache.logging.log4j.Logger;
import utils.LogHelper;
import utils.RequestFactory;

/**Hooks class Initializes RequestSpecification before each scenario
 * and clears logs and thread-local after each scenario for API tests */

public class APIHooks {

    private static final Logger logger = LogHelper.getLogger(APIHooks.class);

    @Before("@API")
    public void setup() {
        logger.info("Initializing API RequestSpecification...");
        RequestFactory.getRequest(); // Thread-safe init
    }

    @After("@API")
    public void tearDown() {
        logger.info("Clearing API RequestSpecification and logs...");
        RequestFactory.clearRequest();
    }
}