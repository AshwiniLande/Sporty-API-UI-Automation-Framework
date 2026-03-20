package pages;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.LogHelper;
import utils.Utils;

import java.time.Duration;
import java.util.List;

/** Page Object representing Twitch homepage and streamer selection actions */
public class TwitchHomePage {

    private static final Logger logger = LogHelper.getLogger(TwitchHomePage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    /** Initializes page object with WebDriver and explicit wait */
    public TwitchHomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }


    private final By browseLink = By.xpath("//div[text()='Browse']/..");
    private final By searchBox = By.xpath("//input[@type='search']");
    private final By viewAllCategoriesLink = By.xpath("//h2[contains(text(),'Categories')]/..//a");
    private final By listItems = By.xpath("//div[@role='list']/div");



    /** Clicks the Browse link after scrolling into view */
    public void clickBrowse() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(browseLink));
        scrollIntoView(element);
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        logger.info("Clicked on Browse");
    }

    /** Clicks on the search box to enable input */
    public void clickSearchIcon() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
        element.click();
        logger.info("Clicked on search box");
    }

    /** Enters the specified game in search box and presses Enter */
    public void searchGame(String game) {
        WebElement box = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBox));
        box.clear();
        box.sendKeys(game);
        box.sendKeys(Keys.ENTER);
        logger.info("Searched for game: {}", game);
    }

    /** Selects the first available streamer from categories */
    public void selectAnyAvailableStreamer() {
        try {
            WebElement viewAll = wait.until(ExpectedConditions.elementToBeClickable(viewAllCategoriesLink));
            scrollIntoView(viewAll);
            viewAll.click();
            logger.info("Clicked 'View All' under Categories");

            // Wait for list items to be visible
            List<WebElement> items = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(listItems));

            if (items.isEmpty()) {
                throw new RuntimeException("No items found after clicking View All");
            }

            WebElement firstItem = wait.until(ExpectedConditions.elementToBeClickable(items.get(0)));
            scrollIntoView(firstItem);
            logger.info("Clicking first streamer: {}", firstItem.getText());
            firstItem.click();

        } catch (TimeoutException e) {
            throw new RuntimeException("Failed to select streamer - elements not loaded in time", e);
        }
    }

    /** Waits for streamer page to load by checking banner visibility with retry using WebDriverWait only */
    public void waitForStreamerPageLoad(String bannerText) {
        int maxRetries = 3;             // Number of attempts
        long waitPerAttempt = 3;        // Wait per attempt in seconds

        By dynamicBannerLocator = By.xpath("//h1[text()='" + bannerText + "']");

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                WebDriverWait retryWait = new WebDriverWait(driver, Duration.ofSeconds(waitPerAttempt));
                WebElement banner = retryWait.until(ExpectedConditions.visibilityOfElementLocated(dynamicBannerLocator));
                logger.info("Streamer page loaded successfully. Banner visible: {}", banner.isDisplayed());
                return;
            } catch (TimeoutException e) {
                logger.warn("Attempt {}: Banner with text '{}' not visible yet.", attempt, bannerText);
                if (attempt == maxRetries) {
                    throw new RuntimeException("Streamer page did not load properly - banner with text '" + bannerText
                                    + "' not visible after " + maxRetries + " attempts", e);
                }
            }
        }
    }

    /** Scrolls the given element into center view using JavaScript */
    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }
}