package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ITestContext;

/** TestNG listener for logging test results to ExtentReports with thread-safe handling */
public class Listener implements ITestListener {

    private static final ExtentReports extent = ExtentManager.getInstance();

    /**Thread-local ExtentTest to support parallel execution*/
    private static final ThreadLocal<ExtentTest> threadTest = new ThreadLocal<>();

    /** Initializes ExtentTest for each test start */
    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = extent.createTest(result.getMethod().getMethodName());
        threadTest.set(test);
    }

    /** Marks the test as passed in the report */
    @Override
    public void onTestSuccess(ITestResult result) {
        threadTest.get().pass("Test Passed: " + result.getMethod().getMethodName());
        threadTest.remove();
    }

    /** Marks the test as failed, and adds screenshot */
    @Override
    public void onTestFailure(ITestResult result) {

        threadTest.get().fail(result.getThrowable());
        var driver = DriverFactory.getDriver();
        String path = Utils.takeScreenshot(driver); // same method reused
        threadTest.get().addScreenCaptureFromPath(path);
        threadTest.remove();
    }

    /** Marks the test as skipped in the report */
    @Override
    public void onTestSkipped(ITestResult result) {
        threadTest.get().skip("Test Skipped: " + result.getMethod().getMethodName());
        threadTest.remove();
    }

    /** Flushes the ExtentReports at the end of all tests */
    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}