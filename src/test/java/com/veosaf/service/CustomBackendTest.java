package com.veosaf.service;

import com.veosaf.Application;
import com.veosaf.service.exception.CustomBusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static com.veosaf.service.CustomBackend.SUCCESS_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class)
public class CustomBackendTest {

    @Autowired
    private CustomBackend customBackend;

    @Test
    public void shouldSuccess() {
        assertEquals(SUCCESS_MESSAGE, getResponse(customBackend.healthyBackend()));
    }

    @Test
    public void shouldRecoverOnTimeout() {
        assertNotEquals(SUCCESS_MESSAGE, getResponse(customBackend.unhealthyBackendOnTimeout()));
    }

    @Test
    public void shouldNotRecoverFromBusinessException() {
        Assertions.assertThrows(CustomBusinessException.class,
                () -> getResponse(customBackend.healthyBackendWithCustomBusinessException()));
    }

    @Test
    public void shouldRecoverFromUnexpectedError() {
        assertNotEquals(SUCCESS_MESSAGE, getResponse(customBackend.unhealthyOnUnexpectedError()));
    }


    private <T> T getResponse(CompletableFuture<T> completableResponse) {
        try {
            return completableResponse.join();
        } catch (CompletionException e) {
            // the only exception that is not recovered is CustomBusinessException
            throw new CustomBusinessException("Business error", e);
        }
    }
}
