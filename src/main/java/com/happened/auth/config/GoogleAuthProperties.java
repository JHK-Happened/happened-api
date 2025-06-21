package com.happened.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "google.auth")
public class GoogleAuthProperties {

    /**
     * application.yml 의
     * google:
     * auth:
     * client-ids:
     * - ...
     * - ...
     */
    private List<String> clientIds;
}