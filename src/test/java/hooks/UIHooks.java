package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.apache.logging.log4j.Logger;
import utils.DriverFactory;
import utils.LogHelper;

/**Hooks class manages test lifecycle Browser starts before scenario and quits after scenario*/
public class UIHooks {

    private static final Logger logger = LogHelper.getLogger(UIHooks.class);

    @Before("@UI")
    public void setup() {
        logger.info("Launching Browser...");
        DriverFactory.getDriver();
    }

    @After("@UI")
    public void tearDown() {
        logger.info("Closing Browser...");
        DriverFactory.quitDriver();
    }
}