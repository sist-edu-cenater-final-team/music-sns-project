package com.github.musicsnsproject.config.sms;

import com.solapi.sdk.SolapiClient;
import com.solapi.sdk.message.service.DefaultMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SmsSenderProperties.class)// 프로퍼티스 스캔 야믈의 값 가져오는
public class SmsConfig {
    private final SmsSenderProperties smsSenderProperties;
    @Bean
    public DefaultMessageService messageServiceWrapper() {
        return SolapiClient.INSTANCE.createInstance(smsSenderProperties.getApiKey(), smsSenderProperties.getApiSecret());
    }
}
