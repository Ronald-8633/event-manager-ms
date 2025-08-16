package br.com.eventmanager.domain.dto;

import br.com.eventmanager.domain.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDTO {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String profileImageUrl;
    private User.UserRole role;
    private User.UserStatus status;
    private List<String> interests;
    private int organizedEventsCount;
    private int attendedEventsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}