package utils;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
/**
 * Architected with Thread-confined Singleton = Prevents inheritance + singleton-like manager + ThreadLocal
 * [Lazy initialization + encapsulated creation logic + explicit cleanup after execution + scalable & thread-safe]
 */

public final class RequestFactory {

    private static final ThreadLocal<RequestSpecification> requestThread = new ThreadLocal<>();
    private static final ThreadLocal<PrintStream> logThread = new ThreadLocal<>();

    private RequestFactory() {}

    /** Returns current RequestSpecification; initializes if not already created */
    public static RequestSpecification getRequest() {
        if (requestThread.get() == null) {
            createRequest();
        }
        return requestThread.get();
    }

    /** Creates encapsulated RequestSpecification with base URI, JSON content type, and logging filters */
    private static void createRequest() {
        PrintStream stream;
        try {
            File logFile = new File("logs/log_" + Thread.currentThread().getId() + ".txt");
            logFile.getParentFile().mkdirs();
            stream = new PrintStream(new FileOutputStream(logFile, true));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create log file for thread: " + Thread.currentThread().getId(), e);
        }

        RequestSpecification request = new RequestSpecBuilder()
                .setBaseUri(ConfigManager.get("baseURI")) // set JSONPlaceholder URI in qa.properties
                .setContentType(ContentType.JSON)
                .addFilter(RequestLoggingFilter.logRequestTo(stream))
                .addFilter(ResponseLoggingFilter.logResponseTo(stream))
                .build();

        requestThread.set(request);
        logThread.set(stream);
    }

    /** Closes log stream and clears ThreadLocal instances */
    public static void clearRequest() {
        PrintStream stream = logThread.get();
        if (stream != null) {
            stream.close();
        }
        logThread.remove();
        requestThread.remove();
    }

}