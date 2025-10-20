package com.github.musicsnsproject.config.sms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "solapi")
@AllArgsConstructor
@Getter
public class SmsSenderProperties {
    private final String apiKey;
    private final String apiSecret;
    private final String domain;
}