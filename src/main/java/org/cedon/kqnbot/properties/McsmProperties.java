package org.cedon.kqnbot.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "kqnbot.mcsm")
public class McsmProperties {

    private String url;
    private String apikey;
    private String daemonId;
    private Map<String, String> instances;
}
