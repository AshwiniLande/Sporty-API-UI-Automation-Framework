package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

/** Manages a singleton ExtentReports instance for reporting */
public final class ExtentManager {

    private static final ExtentReports extent;

    /** Static block initializes ExtentReports once */
    static {
        extent = new ExtentReports();
        ExtentSparkReporter reporter = new ExtentSparkReporter("target/extentreport.html");
        extent.attachReporter(reporter);
    }

    private ExtentManager() {}

    /** Returns the singleton ExtentReports instance */
    public static ExtentReports getInstance() {
        return extent;
    }
}