package br.com.eventmanager.adapter.inbound.broker;

import br.com.eventmanager.adapter.outbound.persistence.EventRepository;
import br.com.eventmanager.config.AbstractRabbitMqTest;
import br.com.eventmanager.config.TestSecurityConfig;
import br.com.eventmanager.domain.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class EventManagerInboundBrokerTestIT extends AbstractRabbitMqTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StreamBridge streamBridge;

    private Event event;

    @BeforeEach
    void setup() {
        eventRepository.deleteAll();

        event = new Event();
        event.setTitle("Test Event");
        event.setDescription("Evento de teste");
        event.setCategoryCode("TEST");
        event.setLocationCode("TEST01");
        event.setStartDate(LocalDateTime.now().plusDays(1));
        event.setEndDate(LocalDateTime.now().plusDays(2));
        event.setMaxCapacity(100);
        event.setCurrentCapacity(0);
        event.setStatus(Event.EventStatus.PUBLISHED);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        event = eventRepository.save(event);
    }

    @Test
    void shouldCancelEventWhenMessageReceived() throws Exception {
        String payload = objectMapper.writeValueAsString(event);

        streamBridge.send("subscribeEventCancel-in-0", MessageBuilder.withPayload(payload).build());

        Thread.sleep(1000);

        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(updatedEvent.getStatus()).isEqualTo(Event.EventStatus.CANCELLED);
    }
}
