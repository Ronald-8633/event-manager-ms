package br.com.eventmanager.adapter.outbound.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class MicroServiceIntegrationConfig {

    private String protocol;
    private String hostname;
    private String port;
    private String path;

    public String buildBaseUrl() {
        if (Objects.isNull(port)) {
            return protocol.concat("://".concat(hostname)).concat("/").concat(path);
        }
        return protocol.concat("://".concat(hostname)).concat(":").concat(port).concat("/").concat(path);
    }

}
