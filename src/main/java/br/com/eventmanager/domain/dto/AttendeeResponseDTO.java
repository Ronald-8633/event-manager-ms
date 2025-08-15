package br.com.eventmanager.domain.dto;

import br.com.eventmanager.domain.Event;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttendeeResponseDTO {

    private String eventId;
    private String eventTitle;
    private String userId;
    private String message;
    private boolean success;
    private LocalDateTime registeredAt;
    private int currentCapacity;
    private int maxCapacity;
    private int remainingSpots;

    public static AttendeeResponseDTO success(Event event, String userId, String message) {
        return AttendeeResponseDTO.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .userId(userId)
                .success(true)
                .message(message)
                .registeredAt(LocalDateTime.now())
                .currentCapacity(event.getCurrentCapacity())
                .maxCapacity(event.getMaxCapacity())
                .remainingSpots(event.getMaxCapacity() - event.getCurrentCapacity())
                .build();
    }

    public static AttendeeResponseDTO failure(Event event, String userId, String message) {
        return AttendeeResponseDTO.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .userId(userId)
                .success(false)
                .message(message)
                .currentCapacity(event.getCurrentCapacity())
                .maxCapacity(event.getMaxCapacity())
                .remainingSpots(event.getMaxCapacity() - event.getCurrentCapacity())
                .build();
    }
}
