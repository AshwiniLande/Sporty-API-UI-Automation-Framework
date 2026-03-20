package utils;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Utility methods for WebDriver operations: page load, scrolling, screenshots */
public class Utils {

    /** Wait for page load with retry logic using while + boolean flag */
    private static final Logger logger = LogHelper.getLogger(Utils.class);
    public static void waitForPageLoadWithRetry(WebDriver driver, int timeoutSeconds) {

        boolean loaded = false;
        int attempt = 0;

        while (attempt < 2 && !loaded) {
            try {

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
                wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
                loaded = true; // page loaded successfully
                return;
            } catch (TimeoutException e) {
                attempt++;
                logger.info("Page not loaded, retry attempt: " + attempt);

                if (attempt >= 2) //throw exception if retries exhausted
                {
                    throw new RuntimeException("Page did not load after " + attempt + " attempts", e);
                }
            }
        }
    }

    /** Captures screenshot with timestamp and returns absolute path */
    public static String takeScreenshot(WebDriver driver) {

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        String fileName = "screenshot_" + timestamp + ".png";
        File dest = new File("target/screenshots/" + fileName);

        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            dest.getParentFile().mkdirs();
            FileUtils.copyFile(src, dest);
            logger.info("Screenshot captured: {}", dest.getAbsolutePath());
            return dest.getAbsolutePath();

        } catch (IOException e) {
            logger.error("Failed to capture screenshot", e);
            throw new RuntimeException("Failed to capture screenshot: " + fileName, e);
        }
    }

    /** Scrolls the window down by 100px and waits for page load to handle flakiness */
    public static void scrollDown(WebDriver driver)
    {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,100)");
        waitForPageLoadWithRetry(driver,3);
    }

}