package com.myl.method.log.service;

import com.myl.method.log.Application;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LogServiceTests {

    @Resource
    private LogService logService;

    @Test
    void error1() {
        var phone = UUID.randomUUID().toString();
        var password = UUID.randomUUID().toString();
        assertThatCode(() -> logService.error1(phone, password)).hasNoSuppressedExceptions();
    }

    @Test
    void success() {
        var phone = UUID.randomUUID().toString();
        assertThatCode(() -> logService.success(phone)).doesNotThrowAnyException();
    }

    @Test
    void ignoreError1() {
        assertThatCode(() -> logService.ignoreError1()).hasNoSuppressedExceptions();
    }

}
