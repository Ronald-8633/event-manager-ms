package br.com.eventmanager.adapter.outbound.broker;

import lombok.AllArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageHeaders;

import static br.com.eventmanager.shared.Constants.RETRIES;
import static br.com.eventmanager.shared.Constants.SUBSCRIBE_EVENT_CANCEL;
import static org.springframework.messaging.support.MessageBuilder.*;

@AllArgsConstructor
@Configuration
public class EventManagerOutBoundBroker {

    private final StreamBridge streamBridge;

    public void postToEventCancel(String payload, MessageHeaders headers, long newRetries) {
        streamBridge.send(SUBSCRIBE_EVENT_CANCEL, withPayload(payload)
                .copyHeaders(headers)
                .setHeader(RETRIES, newRetries)
                .build());
    }
}
