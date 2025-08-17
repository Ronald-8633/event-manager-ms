package br.com.eventmanager.application.service;

import br.com.eventmanager.adapter.outbound.persistence.CategoryRepository;
import br.com.eventmanager.adapter.outbound.persistence.EventRepository;
import br.com.eventmanager.adapter.outbound.persistence.LocationRepository;
import br.com.eventmanager.domain.Category;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.Location;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.valueOf;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSuggestionService {

    @Value("${openai.api.model}")
    private String model;
    @Value("${openai.api.max-tokens}")
    private String maxTokens;
    @Value("${openai.api.temperature}")
    private String temperature;

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final OpenAiService openAiService;

    public String suggestEventsForUser(String userId) {
        try {
            log.info("Gerando sugestões de eventos para usuário: {}", userId);

            List<Event> events = eventRepository.findByStatus(Event.EventStatus.PUBLISHED);

            if (events.isEmpty()) {
                return "Não há eventos publicados no momento.";
            }

            String context = createContext(events);
            List<ChatMessage> messages = createPrompt(userId, context);
            ChatCompletionRequest request = buildRequest(messages);

            log.debug("Enviando requisição para OpenAI com {} eventos", events.size());
            ChatCompletionResult result = openAiService.createChatCompletion(request);

            String suggestion = result.getChoices().getFirst().getMessage().getContent();
            log.info("Sugestões geradas com sucesso para usuário: {}", userId);

            return suggestion;

        } catch (Exception e) {
            log.error("Erro ao gerar sugestões para usuário: {}", userId, e);
            return "Desculpe, não foi possível gerar sugestões no momento. Tente novamente mais tarde.";
        }
    }

    private ChatCompletionRequest buildRequest(List<ChatMessage> messages) {
        return ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(valueOf(maxTokens))
                .temperature(Double.valueOf(temperature))
                .build();
    }

    private String createContext(List<Event> events) {
        return events.stream()
                .limit(10)
                .map(event -> {
                    Category category = categoryRepository
                            .findByCategoryCode(event.getCategoryCode())
                            .orElse(null);

                    Location location = locationRepository
                            .findByLocationCode(event.getLocationCode())
                            .orElse(null);

                    return mapEventFields(event, category, location);
                })
                .collect(Collectors.joining("\n---\n"));
    }

    private String mapEventFields(Event event, Category category, Location location) {
        return String.format("""
                        Evento: %s
                        Descrição: %s
                        Categoria: %s
                        Local: %s, %s - %s (%s)
                        Data: %s até %s
                        Preço: %s
                        Vagas: %d/%d
                        Status: %s
                        Tags: %s
                        """,
                event.getTitle(),
                event.getDescription(),
                category != null ? category.getName() : "Categoria não encontrada",
                location != null ? location.getName() : "Local não encontrado",
                location != null ? location.getCity() : "-",
                location != null ? location.getState() : "-",
                location != null ? location.getCountry() : "-",
                event.getStartDate(),
                event.getEndDate(),
                event.getPrice() != null ? "R$ " + event.getPrice() : "Gratuito",
                event.getCurrentCapacity(),
                event.getMaxCapacity(),
                event.getStatus(),
                event.getTags() != null ? String.join(", ", event.getTags()) : "-"
        );
    }

    private List<ChatMessage> createPrompt(String userId, String context) {
        return List.of(
                new ChatMessage("system", """
                        Você é um assistente especialista em recomendar eventos para usuários.
                        Considere interesses do usuário, localização, preço e categoria.
                        Sempre sugira no máximo 3 eventos relevantes.
                        Explique de forma breve e amigável por que o evento foi sugerido.
                        Responda em português.
                        Seja conciso e direto ao ponto.
                        """),
                new ChatMessage("user", "Sugira os melhores eventos para o usuário " + userId +
                        " com base nos seguintes dados:\n" + context)
        );
    }
}
