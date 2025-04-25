package com.myl.method.log.service;

import com.myl.method.log.Application;
import com.github.jsonzou.jmockdata.JMockData;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LogServiceTests {

    @Resource
    private LogService logService;

    @Test
    void error1() {
        var phone = JMockData.mock(String.class);
        var password = JMockData.mock(String.class);
        assertThatCode(() -> logService.error1(phone, password)).hasNoSuppressedExceptions();
    }

    @Test
    void success() {
        var phone = JMockData.mock(String.class);
        assertThatCode(() -> logService.success(phone)).doesNotThrowAnyException();
    }

    @Test
    void ignoreError1() {
        assertThatCode(() -> logService.ignoreError1()).hasNoSuppressedExceptions();
    }

}
