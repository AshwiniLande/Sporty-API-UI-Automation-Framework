package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/** Implements smart retry for failed tests based on predefined retryable exceptions and MAX_RETRY */
public final class Retry implements IRetryAnalyzer {

    private int retry = 0;
    private static final int MAX_RETRY = 2;

    private static final Logger logger = LogHelper.getLogger(Retry.class);


    @Override
    public boolean retry(ITestResult result) {

        Throwable error = result.getThrowable();

        if (error == null) {
            return false;
        }

        String message = error.getMessage() != null ? error.getMessage() : "";

        if (retry < MAX_RETRY && isRetryable(message)) {
            retry++;
            logger.info("Retrying test: {} | Attempt: {}", result.getName(), retry);
            return true;
        }
        return false;
    }

    /** Checks if the exception message matches retryable UI or API errors */
    private boolean isRetryable(String message) {
        List<String> errors = Arrays.asList("NoSuchElement", "ElementNotInteractable", "StaleElementReference",
                "Timeout", "500 Internal Server Error","503 Service Unavailable");
        return errors.stream().anyMatch(message::contains);
    }
}