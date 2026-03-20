package stepdefinitions;

import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import pages.TwitchHomePage;
import utils.ConfigManager;
import utils.DriverFactory;
import utils.LogHelper;
import utils.Utils;

import org.apache.logging.log4j.Logger;

/** Step Definitions for Twitch Search Scenario */
public class TwitchSteps {
    private static final Logger logger = LogHelper.getLogger(TwitchSteps.class);

    private WebDriver driver = DriverFactory.getDriver();
    private TwitchHomePage homePage = new TwitchHomePage(driver);

    /** Opens Twitch homepage and waits for full page load */
    @Given("user opens Twitch website")
    public void openTwitchWebsite() {
        driver.get(ConfigManager.get("baseUrl"));
        driver.manage().window().fullscreen();
        Utils.waitForPageLoadWithRetry(driver, 10);
    }

    /** Clicks browse and search icons on homepage */
    @When("user clicks search icon")
    public void clickSearchIcon() {
        homePage.clickBrowse();
        homePage.clickSearchIcon();
    }

    /** Searches for the specified game */
    @And("user searches for {string}")
    public void searchGame(String game) {
        homePage.searchGame(game);
    }

    /** Scrolls down the page specified number of times */
    @And("user scrolls down {int} times")
    public void scrollDown(int nbTimes) throws InterruptedException
    {
        if (nbTimes <= 0) {
            throw new IllegalArgumentException("Scroll times must be greater than zero");
        }

        for(int i=0;i<nbTimes;i++) {
            Utils.scrollDown(driver);
        }
    }

    /** Selects any available streamer from search results */
    @And("user selects a streamer")
    public void selectStreamer() {
        homePage.selectAnyAvailableStreamer();
    }

    /** Waits for streamer page to load and logs the page title */
    @Then("streamer page should load with text {string}")
    public void streamerPageLoad(String expectedText) {
        homePage.waitForStreamerPageLoad(expectedText);
        logger.info("Streamer page loaded: " + driver.getTitle());
    }

    /** Captures screenshot after ensuring page is loaded */
    @And("screenshot should be taken")
    public void takeScreenshot() throws Exception {
        Utils.waitForPageLoadWithRetry(driver,10);
        Utils.takeScreenshot(driver);
    }
}