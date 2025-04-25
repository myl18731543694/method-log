package com.myl.method.log.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = "bulade.log-exception")
public class LogConfig {

    private Set<String> ignoreExceptions = new HashSet<>();

}
