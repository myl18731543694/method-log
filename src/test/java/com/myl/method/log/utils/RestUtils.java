package com.myl.method.log.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Component
@Slf4j
public class RestUtils {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private TestRestTemplate testRestTemplate;

    public RestMultipartFileHelper get(String url, Object... params) {
        var param = params.length > 0 ? params[0] : null;
        var restContext = new RestContext()
            .setApplicationContext(applicationContext)
            .setObjectMapper(objectMapper)
            .setTestRestTemplate(testRestTemplate)
            .setUrl(url)
            .setHttpMethod(HttpMethod.GET)
            .setParam(param);
        return new RestMultipartFileHelper(restContext);
    }

    public RestMultipartFileHelper post(String url, Object... params) {
        var param = params.length > 0 ? params[0] : null;
        var restContext = new RestContext()
            .setApplicationContext(applicationContext)
            .setObjectMapper(objectMapper)
            .setTestRestTemplate(testRestTemplate)
            .setUrl(url)
            .setHttpMethod(HttpMethod.POST)
            .setParam(param);
        return new RestMultipartFileHelper(restContext);
    }

    public RestMultipartFileHelper put(String url, Object... params) {
        var param = params.length > 0 ? params[0] : null;
        var restContext = new RestContext()
            .setApplicationContext(applicationContext)
            .setObjectMapper(objectMapper)
            .setTestRestTemplate(testRestTemplate)
            .setUrl(url)
            .setHttpMethod(HttpMethod.PUT)
            .setParam(param);
        return new RestMultipartFileHelper(restContext);
    }

    public RestMultipartFileHelper delete(String url, Object... params) {
        var param = params.length > 0 ? params[0] : null;
        var restContext = new RestContext()
            .setApplicationContext(applicationContext)
            .setObjectMapper(objectMapper)
            .setTestRestTemplate(testRestTemplate)
            .setUrl(url)
            .setHttpMethod(HttpMethod.DELETE)
            .setParam(param);
        return new RestMultipartFileHelper(restContext);
    }

    @Data
    @Accessors(chain = true)
    public static class RestContext {
        private ApplicationContext applicationContext;

        private ObjectMapper objectMapper;

        private TestRestTemplate testRestTemplate;

        private String url;

        private Map<String, Object> headers = new HashMap<>();

        private HttpMethod httpMethod;

        private Object param;

        private String token;

        private Map<String, File> fileMap = new HashMap<>();
    }

    public static class RestMultipartFileHelper extends RestLoginHelper {

        private final RestContext restContext;

        public RestMultipartFileHelper(RestContext restContext) {
            super(restContext);
            this.restContext = restContext;
        }

        public RestLoginHelper multipartFile(String name, File file) {
            restContext.getFileMap().put(name, file);
            return this;
        }

    }

    public static class RestLoginHelper extends RestResultHelper {

        private final RestContext restContext;

        public RestLoginHelper(RestContext restContext) {
            super(restContext);
            this.restContext = restContext;
        }

        public RestLoginHelper header(String headerName, Object headerValue) {
            restContext.getHeaders().put(headerName, headerValue);
            return this;
        }

    }

    @AllArgsConstructor
    public static class RestResultHelper {

        private RestContext restContext;

        @SneakyThrows
        public <T> T result(TypeReference<T> typeRef) {
            // 执行HTTP请求
            var extract = execHttp();
            var body = extract.getBody();
            assertThat(body).isNotNull();
            log.info("返回结果 {}", new String(body, StandardCharsets.UTF_8));

            var objectReader = restContext.getObjectMapper().reader();
            return objectReader.forType(typeRef).readValue(body);
        }

        private ResponseEntity<byte[]> execHttp() {
            var url = restContext.getUrl();
            if (!url.startsWith("/")) {
                url = "/" + url;
            }
            var httpMethod = restContext.getHttpMethod();
            Object body = null;
            var headers = new HttpHeaders();
            var urlVariables = new HashMap<String, Object>();

            // 设置http请求参数
            if (restContext.getParam() != null) {
                if (HttpMethod.GET.equals(httpMethod)) {
                    urlVariables.putAll(ParamUtils.build(restContext.getParam()));
                    if (!urlVariables.isEmpty()) {
                        var tempListUrlVariables = new HashMap<String, Object>();
                        var param = urlVariables
                            .keySet()
                            .stream()
                            .map(t -> {
                                // 传入的如果是list类型，需要进行特殊转换
                                var value = urlVariables.get(t);
                                if (value instanceof List<?> list) {
                                    return list
                                        .stream()
                                        .map(v -> {
                                            var generateValueKey = t + "_" + UUID.randomUUID();
                                            tempListUrlVariables.put(generateValueKey, v);
                                            return "%s={%s}".formatted(t, generateValueKey);
                                        })
                                        .collect(Collectors.joining("&"));
                                }
                                return "%s={%s}".formatted(t, t);
                            })
                            .collect(Collectors.joining("&"));
                        url += "?" + param;
                        urlVariables.putAll(tempListUrlVariables);
                    }
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                } else {
                    body = restContext.getParam();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                }
            }

            // 附件上传判断
            for (Map.Entry<String, File> entry : restContext.getFileMap().entrySet()) {
                urlVariables.put(entry.getKey(), entry.getValue());
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            }

            // 指定登录用户token
            if (!(restContext.getToken() == null || restContext.getToken().isEmpty())) {
                headers.add("Authorization", restContext.getToken());
            }

            // 指定header
            for (Map.Entry<String, Object> entry : restContext.getHeaders().entrySet()) {
                headers.add(entry.getKey(), entry.getValue().toString());
            }

            // 设置请求地址，执行货币返回结果
            var httpEntity = new HttpEntity<>(body, headers);
            var typeReference = new ParameterizedTypeReference<byte[]>() {
            };
            return restContext.getTestRestTemplate().exchange(
                url,
                httpMethod,
                httpEntity,
                typeReference,
                urlVariables
            );
        }

    }

    private static class ParamUtils {

        private ParamUtils() {
        }

        @SneakyThrows
        public static Map<String, Object> build(Object object) {
            var map = new HashMap<String, Object>();
            var fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                var value = field.get(object);
                if (value != null) {
                    if (value instanceof LocalDateTime localDateTime) {
                        var format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        map.put(field.getName(), format);
                        continue;
                    }

                    map.put(field.getName(), value);
                }
            }
            return map;
        }

    }

}
