package br.com.eventmanager.adapter.inbound.broker;

import br.com.eventmanager.adapter.outbound.broker.EventManagerOutBoundBroker;
import br.com.eventmanager.application.service.EventService;
import br.com.eventmanager.domain.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class EventManagerInboundBroker {

    private final ObjectMapper mapper;
    private final EventService eventService;
    private final EventManagerOutBoundBroker outBound;

    @Bean
    Consumer<Message<String>> subscribeEventCancel() {
        return message -> {
            var payload = message.getPayload();
            MessageHeaders headers = message.getHeaders();

            Long retries = headers.get("retries", Long.class);
            retries = (retries == null) ? 0L : retries;
            try {
                Event event = deserializeEvent(payload);
                eventService.cancelEvent(event.getId());
            } catch (Exception e) {
                long newRetries = retries + 1;
                if (newRetries >= 4) {
                    throw new AmqpRejectAndDontRequeueException("Max retries reached", e);
                } else {
                    outBound.postToEventCancel(payload, headers, newRetries);
                }
            }
        };
    }

    private Event deserializeEvent(String payload) throws JsonProcessingException {
        return mapper
                .registerModule(new JavaTimeModule())
                .readValue(payload, Event.class);
    }
}
