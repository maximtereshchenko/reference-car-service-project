package com.andersenlab.extension;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import java.util.concurrent.atomic.AtomicBoolean;

public final class SkipRemainingOnFailureExtension implements TestExecutionExceptionHandler, ExecutionCondition {

    private final AtomicBoolean shouldSkip = new AtomicBoolean(false);

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (shouldSkip.get()) {
            return ConditionEvaluationResult.disabled("One of the previous tests failed");
        }
        return ConditionEvaluationResult.enabled("No tests failed so far");
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        shouldSkip.set(true);
        throw throwable;
    }
}
