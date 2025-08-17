package br.com.eventmanager.application.service;

import br.com.eventmanager.adapter.outbound.externalApi.OpenAiClient;
import br.com.eventmanager.adapter.outbound.persistence.CategoryRepository;
import br.com.eventmanager.adapter.outbound.persistence.EventRepository;
import br.com.eventmanager.adapter.outbound.persistence.LocationRepository;
import br.com.eventmanager.domain.Category;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.Location;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventSuggestionService Tests")
class EventSuggestionServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private OpenAiClient openAiClient;

    @Mock
    private OpenAiService openAiService;

    @InjectMocks
    private EventSuggestionService eventSuggestionService;

    private Event event;
    private Category category;
    private Location location;
    private LocalDateTime now;
    private LocalDateTime tomorrow;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        tomorrow = now.plusDays(1);

        ReflectionTestUtils.setField(eventSuggestionService, "model","gpt-4o-mini");
        ReflectionTestUtils.setField(eventSuggestionService, "maxTokens","100");
        ReflectionTestUtils.setField(eventSuggestionService, "temperature","10");

        event = Event.builder()
                .id("event-123")
                .title("Workshop Spring Boot")
                .description("Aprenda Spring Boot do zero")
                .categoryCode("TECH")
                .locationCode("SP-001")
                .startDate(tomorrow)
                .endDate(tomorrow.plusHours(3))
                .maxCapacity(50)
                .currentCapacity(25)
                .price(99.99)
                .status(Event.EventStatus.PUBLISHED)
                .tags(Arrays.asList("java", "spring", "workshop"))
                .createdAt(now)
                .updatedAt(now)
                .build();

        category = Category.builder()
                .id("TECH")
                .name("Tecnologia")
                .description("Eventos de tecnologia")
                .build();

        location = Location.builder()
                .id("SP-001")
                .name("Centro de Eventos SP")
                .city("São Paulo")
                .state("SP")
                .country("Brasil")
                .build();
    }

    @Nested
    @DisplayName("Context Creation Tests")
    class ContextCreationTests {

        @Test
        @DisplayName("Should create context with event information")
        void shouldCreateContextWithEventInformation() {
            List<Event> events = Arrays.asList(event);
            when(categoryRepository.findByCategoryCode("TECH")).thenReturn(Optional.of(category));
            when(locationRepository.findByLocationCode("SP-001")).thenReturn(Optional.of(location));

            String context = eventSuggestionService.createContext(events);

            assertNotNull(context);
            assertTrue(context.contains("Workshop Spring Boot"));
            assertTrue(context.contains("Aprenda Spring Boot do zero"));
            assertTrue(context.contains("Tecnologia"));
            assertTrue(context.contains("Centro de Eventos SP"));
            assertTrue(context.contains("São Paulo"));
            assertTrue(context.contains("R$ 99.99"));
            assertTrue(context.contains("25/50"));
        }

        @Test
        @DisplayName("Should handle null tags gracefully")
        void shouldHandleNullTagsGracefully() {
            event.setTags(null);
            List<Event> events = Arrays.asList(event);
            when(categoryRepository.findByCategoryCode("TECH")).thenReturn(Optional.of(category));
            when(locationRepository.findByLocationCode("SP-001")).thenReturn(Optional.of(location));

            String context = eventSuggestionService.createContext(events);

            assertNotNull(context);
            assertTrue(context.contains("-"));
        }

        @Test
        @DisplayName("Should handle null price")
        void shouldHandleNullPriceGracefully() {
            event.setPrice(null);
            List<Event> events = Arrays.asList(event);
            when(categoryRepository.findByCategoryCode("TECH")).thenReturn(Optional.of(category));
            when(locationRepository.findByLocationCode("SP-001")).thenReturn(Optional.of(location));

            String context = eventSuggestionService.createContext(events);

            assertNotNull(context);
            assertTrue(context.contains("Gratuito"));
        }
    }

    @Nested
    @DisplayName("Prompt Creation Tests")
    class PromptCreationTests {

        @Test
        @DisplayName("Should create prompt with system and user messages")
        void shouldCreatePromptWithSystemAndUserMessages() {
            String userId = "user-123";
            String context = "Contexto dos eventos";

            List<ChatMessage> messages = eventSuggestionService.createPrompt(userId, context);

            assertEquals(2, messages.size());

            ChatMessage systemMessage = messages.get(0);
            assertEquals("system", systemMessage.getRole());
            assertTrue(systemMessage.getContent().contains("assistente especialista"));
            assertTrue(systemMessage.getContent().contains("português"));

            ChatMessage userMessage = messages.get(1);
            assertEquals("user", userMessage.getRole());
            assertTrue(userMessage.getContent().contains(userId));
            assertTrue(userMessage.getContent().contains(context));
        }

        @Test
        @DisplayName("Should include user ID in prompt")
        void shouldIncludeUserIdInPrompt() {
            String userId = "user-123";
            String context = "Contexto dos eventos";

            List<ChatMessage> messages = eventSuggestionService.createPrompt(userId, context);

            ChatMessage userMessage = messages.get(1);
            assertTrue(userMessage.getContent().contains(userId));
        }
    }
}