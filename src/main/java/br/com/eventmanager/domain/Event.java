package br.com.eventmanager.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "events")
public class Event {
    
    @Id
    private String id;
    
    private String title;
    private String description;
    private String categoryCode;
    private String locationCode;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxCapacity;
    private Integer currentCapacity;
    private Double price;
    private String organizerId;
    private EventStatus status;
    private List<String> tags;
    private Set<String> attendees;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum EventStatus {
        DRAFT, PUBLISHED, CANCELLED, COMPLETED
    }
}
