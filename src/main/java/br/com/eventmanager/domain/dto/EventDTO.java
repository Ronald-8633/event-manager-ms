package br.com.eventmanager.domain.dto;

import br.com.eventmanager.domain.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private String id;
    private String title;
    private String description;
    private CategoryDTO category;
    private LocationDTO location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxCapacity;
    private Integer currentCapacity;
    private Double price;
    private String organizerId;
    private Event.EventStatus status;
    private List<String> tags;
    private Set<String> attendees;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
