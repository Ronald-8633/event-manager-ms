package br.com.eventmanager.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;

    private String entityType;
    private String entityId;
    private String operation;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private String userId;
    private LocalDateTime timestamp;
    private String details;
}