package com.myl.method.log.controller;

import com.myl.method.log.Application;
import com.myl.method.log.controller.req.LogTestReq;
import com.myl.method.log.utils.RestUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.Map;
import java.util.UUID;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LogControllerTests {

    @Resource
    private LogController logController;

    @Resource
    private RestUtils restUtils;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private TestRestTemplate testRestTemplate;

    /**
     * @see LogController#error1(LogTestReq)
     */
    @Test
    void error1() {
        var logTestReq = new LogTestReq()
            .setPhone(UUID.randomUUID().toString())
            .setPassword(UUID.randomUUID().toString());
        var commonResponse = restUtils
            .post("/api/admin/log/error1", logTestReq)
            .result(new TypeReference<Map<String, String>>() {
            });
        Assertions.assertThat(commonResponse).containsValue("方法未实现，暂不支持调用");
    }

}
