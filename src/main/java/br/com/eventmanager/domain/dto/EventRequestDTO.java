package br.com.eventmanager.domain.dto;

import br.com.eventmanager.domain.Event;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventRequestDTO {

    @NotEmpty
    private String title;
    @NotEmpty
    private String description;
    private String categoryCode;
    private String locationCode;
    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;
    @NotNull
    @Min(0)
    private Integer maxCapacity;
    private Integer currentCapacity;
    @Min(0)
    private Double price;
    @NotEmpty
    private String organizerId;
    private Event.EventStatus status;
    private List<String> tags;
    private Set<String> attendees;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
