package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Architected with Thread-confined Singleton = Prevents inheritance + singleton-like manager + ThreadLocal
 * [Lazy initialization + encapsulated creation logic + explicit cleanup after execution + scalable & thread-safe]
 */

public final class DriverFactory {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    private DriverFactory() {}

    /** Returns the current WebDriver instance.If not created yet, it initializes the driver.*/
    public static WebDriver getDriver() {

        if (driver.get() == null) {
            createDriver();
        }
        return driver.get();
    }

    /** Creates encapsulated WebDriver based on configuration.*/
    private static void createDriver() {

        String browser = ConfigManager.get("browser").toLowerCase();

        switch (browser.toLowerCase()) { //Easy for other browser addition
            case "chrome":
                WebDriverManager.chromedriver().setup();

                Map<String, String> mobileEmulation = new HashMap<>();
                mobileEmulation.put("deviceName", "iPhone X");

                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);

                driver.set(new ChromeDriver(chromeOptions));
                break;

            default:
                throw new RuntimeException("Unsupported browser: " + browser);
        }
    }

    /** Quit browser and clean ThreadLocal instance. */
    public static void quitDriver() {
        if (driver.get() != null)
        {
            driver.get().quit();
            driver.remove();
        }
    }
}