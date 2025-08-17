package br.com.eventmanager.adapter.inbound.rest.event;


import br.com.eventmanager.adapter.outbound.persistence.EventRepository;
import br.com.eventmanager.adapter.outbound.persistence.UserRepository;
import br.com.eventmanager.config.AbstractIntegrationTest;
import br.com.eventmanager.config.TestSecurityConfig;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.User;
import br.com.eventmanager.domain.dto.EventRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class EventControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private EventRequestDTO eventRequest;
    private User testUser;

    @BeforeEach
    void setup() {
        eventRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .id("test-user-123")
                .name("Test User")
                .email("test@email.com")
                .password("hashedPassword")
                .role(User.UserRole.ORGANIZER)
                .status(User.UserStatus.ACTIVE)
                .interests(Arrays.asList("tecnologia", "eventos"))
                .organizedEvents(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(testUser);

        eventRequest = EventRequestDTO.builder()
                .title("Spring Boot Conf")
                .description("Evento de tecnologia")
                .categoryCode("TECH")
                .locationCode("SP01")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .maxCapacity(100)
                .price(50.0)
                .organizerId(testUser.getEmail())
                .build();
    }

    @Test
    @WithMockUser(username = "test@email.com", roles = {"ORGANIZER"})
    void shouldCreateEventSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(eventRequest.getTitle()));
    }

    @Test
    @WithMockUser(username = "test@email.com", roles = {"ORGANIZER"})
    void shouldGetEventById() throws Exception {

        Event event = Event.builder()
                .title("Java Meetup")
                .description("Meetup de Java")
                .categoryCode("JAVA")
                .locationCode("RJ01")
                .startDate(LocalDateTime.now().plusDays(3))
                .endDate(LocalDateTime.now().plusDays(4))
                .maxCapacity(50)
                .currentCapacity(0)
                .status(Event.EventStatus.PUBLISHED)
                .organizerId(testUser.getEmail())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Event saved = eventRepository.save(event);

        mockMvc.perform(get("/api/v1/events/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(event.getTitle()));
    }

    @Test
    @WithMockUser(username = "test@email.com", roles = {"ORGANIZER"})
    void shouldReturn404WhenEventNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/events/234423"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "test@email.com", roles = {"ORGANIZER"})
    void shouldAccessApiEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk());
    }
}