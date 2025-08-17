package br.com.eventmanager.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatRequestDTO {
    private String model;
    private List<Message> messages;

    public ChatRequestDTO(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
    }

    @Data
    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
