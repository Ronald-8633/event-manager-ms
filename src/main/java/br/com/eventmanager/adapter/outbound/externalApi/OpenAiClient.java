package br.com.eventmanager.adapter.outbound.externalApi;

import br.com.eventmanager.domain.dto.ChatRequestDTO;
import br.com.eventmanager.domain.dto.ChatResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class OpenAiClient {

    private final WebClient webClient;

    public OpenAiClient(WebClient openAiWebClient) {
        this.webClient = openAiWebClient;
    }

    public String getChatCompletion(String prompt) {
        ChatRequestDTO request = new ChatRequestDTO(
                "gpt-4o-mini",
                List.of(new ChatRequestDTO.Message("user", prompt))
        );

        ChatResponseDTO response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponseDTO.class)
                .block();

        if (response != null && !response.getChoices().isEmpty()) {
            return response.getChoices().getFirst().getMessage().getContent();
        }
        return "Nenhuma sugestão disponível.";
    }
}
