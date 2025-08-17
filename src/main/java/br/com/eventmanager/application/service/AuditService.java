package br.com.eventmanager.application.service;

import br.com.eventmanager.adapter.outbound.persistence.AuditLogRepository;
import br.com.eventmanager.domain.AuditLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.com.eventmanager.shared.Constants.CSV_DATE_FORMAT;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.TEXT_PLAIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void logChange(String entityType, String entityId, String operation,
                          String fieldName, String oldValue, String newValue, String userId) {

        AuditLog auditLog = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .operation(operation)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .details(String.format("%s %s on %s", operation, entityType, entityId))
                .build();

        auditLogRepository.save(auditLog);
        log.info("Audit log created: {}", auditLog);
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void exportChangesToFile() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime today = LocalDateTime.now();

        List<AuditLog> changes = auditLogRepository.findByTimestampBetween(yesterday, today);

        if (changes.isEmpty()) {
            log.info("No changes detected for export");
            return;
        }

        String fileName = String.format("audit_changes_%s.csv",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            write(writer, changes, fileName);

        } catch (IOException e) {
            log.error("Error exporting audit changes to file", e);
        }
    }

    public ResponseEntity<Resource> exportTodayChangesAndDownload() {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = LocalDateTime.now();

        var changes = auditLogRepository.findByTimestampBetween(start, end);

        if (changes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        String fileName = String.format("audit_changes_%s_to_%s.csv",
                start.format(CSV_DATE_FORMAT), end.format(CSV_DATE_FORMAT));

        String csvContent = buildCsv(changes);

        var resource = new ByteArrayResource(csvContent.getBytes(UTF_8));

        return ResponseEntity.ok()
                .header(CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(TEXT_PLAIN)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    private String buildCsv(List<AuditLog> changes) {
        String header = "Timestamp,Entity Type,Entity ID,Operation,Field Name,Old Value,New Value,User ID,Details";

        String body = changes.stream()
                .map(this::toCsvLine)
                .collect(Collectors.joining("\n"));

        return header + "\n" + body;
    }

    private void write(BufferedWriter writer, List<AuditLog> changes, String fileName) throws IOException {
        writer.write("Timestamp,Entity Type,Entity ID,Operation,Field Name,Old Value,New Value,User ID,Details\n");

        changes.stream()
                .map(this::toCsvLine)
                .forEach(line -> {
                    try {
                        writer.write(line);
                        writer.newLine();
                    } catch (IOException e) {
                        log.error("Error writing line to CSV", e);
                    }
                });

        log.info("Successfully exported {} changes to file: {}", changes.size(), fileName);
    }

    private String writeCSV(BufferedWriter writer, List<AuditLog> changes, String fileName) throws IOException {
        writer.write("Timestamp,Entity Type,Entity ID,Operation,Field Name,Old Value,New Value,User ID,Details\n");

        changes.stream()
                .map(this::toCsvLine)
                .forEach(line -> {
                    try {
                        writer.write(line);
                        writer.newLine();
                    } catch (IOException e) {
                        log.error("Error writing line to CSV", e);
                    }
                });

        return String.format("Successfully exported %d changes to file: %s", changes.size(), fileName);
    }

    public void logIfChanged(String entity, String id, String fieldName, Object oldValue, Object newValue, String email) {
        if (oldValue == null && newValue == null) return;
        if ((oldValue == null && newValue != null) || (oldValue != null && !oldValue.equals(newValue))) {
            this.logChange(
                    entity,
                    id,
                    "UPDATE",
                    fieldName,
                    oldValue != null ? oldValue.toString() : null,
                    newValue != null ? newValue.toString() : null,
                    email
            );
        }
    }

    private String toCsvLine(AuditLog change) {
        return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                change.getTimestamp(),
                change.getEntityType(),
                change.getEntityId(),
                change.getOperation(),
                Objects.toString(change.getFieldName(), EMPTY),
                Objects.toString(change.getOldValue(), EMPTY),
                Objects.toString(change.getNewValue(), EMPTY),
                Objects.toString(change.getUserId(), EMPTY),
                Objects.toString(change.getDetails(), EMPTY)
        );
    }
}
