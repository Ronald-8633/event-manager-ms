package br.com.eventmanager.adapter.inbound.broker;

import br.com.eventmanager.adapter.outbound.broker.EventManagerOutBoundBroker;
import br.com.eventmanager.application.service.EventService;
import br.com.eventmanager.application.service.MessageService;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.shared.Constants;
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

import static br.com.eventmanager.shared.Constants.RETRIES;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class EventManagerInboundBroker {

    private final ObjectMapper mapper;
    private final EventService eventService;
    private final EventManagerOutBoundBroker outBound;
    private final MessageService messageService;

    @Bean
    Consumer<Message<String>> subscribeEventCancel() {
        return message -> {
            var payload = message.getPayload();
            MessageHeaders headers = message.getHeaders();

            Long retries = getRetries(headers);
            try {
                Event event = deserializeEvent(payload);
                eventService.cancelEvent(event.getId());
            } catch (Exception e) {
                long newRetries = retries + 1;
                if (newRetries >= 4) {
                    throw new AmqpRejectAndDontRequeueException(messageService.getMessage(Constants.EM_0023), e);
                } else {
                    outBound.postToEventCancel(payload, headers, newRetries);
                }
            }
        };
    }

    private static Long getRetries(MessageHeaders headers) {
        Long retries = headers.get(RETRIES, Long.class);
        retries = (retries == null) ? 0L : retries;
        return retries;
    }

    private Event deserializeEvent(String payload) throws JsonProcessingException {
        return mapper
                .registerModule(new JavaTimeModule())
                .readValue(payload, Event.class);
    }
}
