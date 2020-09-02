package com.uvcity.smartpark.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author chenling
 */
@Data
@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfig {

    private String host;
    private String clientId;
    private String ipcTopic;
    private String rsuTopic;
}
