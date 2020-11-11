package com.veosaf.service;

import com.veosaf.service.exception.CustomBusinessException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class CustomBackend {

    static final String CUSTOM_BACKEND = "customBackend";
    static final String SUCCESS_MESSAGE = "success-custom-backend";

    @Bulkhead(name = CUSTOM_BACKEND)
    @TimeLimiter(name = CUSTOM_BACKEND)
    @CircuitBreaker(name = CUSTOM_BACKEND, fallbackMethod = "customFallback")
    public CompletableFuture<String> healthyBackend() {
        return CompletableFuture.completedFuture(SUCCESS_MESSAGE);
    }

    @Bulkhead(name = CUSTOM_BACKEND)
    @TimeLimiter(name = CUSTOM_BACKEND, fallbackMethod = "customFallback")
    @CircuitBreaker(name = CUSTOM_BACKEND, fallbackMethod = "customFallback")
    public CompletableFuture<String> unhealthyBackendWithTimeout() {
        return CompletableFuture.supplyAsync(() -> {
            Try.run(() -> Thread.sleep(2000));
            return SUCCESS_MESSAGE;
        });
    }

    @Bulkhead(name = CUSTOM_BACKEND)
    @TimeLimiter(name = CUSTOM_BACKEND)
    @CircuitBreaker(name = CUSTOM_BACKEND, fallbackMethod = "customFallback")
    public CompletableFuture<String> healthyBackendWithCustomBusinessException() {
        return CompletableFuture.supplyAsync(() -> {
            throw new CustomBusinessException("Business exception");
        });
    }

    @Bulkhead(name = CUSTOM_BACKEND)
    @TimeLimiter(name = CUSTOM_BACKEND)
    @CircuitBreaker(name = CUSTOM_BACKEND, fallbackMethod = "customFallback")
    public CompletableFuture<String> unhealthyBackendWithErrorException() {
        return CompletableFuture.supplyAsync(() -> {
            throw new IllegalArgumentException("Business exception");
        });
    }

    private CompletableFuture<String> customFallback(Throwable ex) {
        return CompletableFuture.completedFuture("Enter Recovery Mode : " + ex.toString());
    }

    private CompletableFuture<String> customFallback(TimeoutException ex) {
        return CompletableFuture.failedFuture(ex);
    }

    private CompletableFuture<String> customFallback(CustomBusinessException ex) {
        // It is an ignored exception (see yaml conf)
        // It is ignored and neither count as a failure nor success
        // No Recovery Mode for business exception
        return CompletableFuture.failedFuture(ex);
    }


}
