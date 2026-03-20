package utils;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Method;

/** Centralized sets Retry.class as the default RetryAnalyzer */
public final class Transform implements IAnnotationTransformer {

    public void transform(ITestAnnotation annotation, Class testClass, Method testMethod) {

        if (annotation.getRetryAnalyzerClass() == null) {
            annotation.setRetryAnalyzer(Retry.class);
        }
    }
}