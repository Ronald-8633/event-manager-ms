package br.com.eventmanager.application.service;

import br.com.eventmanager.adapter.outbound.persistence.AuditLogRepository;
import br.com.eventmanager.domain.AuditLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditService auditService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogChange_SavesAuditLog() {
        auditService.logChange("EntityType", "123", "CREATE", "field", "old", "new", "user@test.com");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository, times(1)).save(captor.capture());

        AuditLog log = captor.getValue();
        assertEquals("EntityType", log.getEntityType());
        assertEquals("123", log.getEntityId());
        assertEquals("CREATE", log.getOperation());
        assertEquals("field", log.getFieldName());
        assertEquals("old", log.getOldValue());
        assertEquals("new", log.getNewValue());
        assertEquals("user@test.com", log.getUserId());
        assertNotNull(log.getTimestamp());
        assertTrue(log.getDetails().contains("CREATE EntityType on 123"));
    }

    @Test
    void testLogIfChanged_WhenValuesDiffer_ShouldCallLogChange() {
        auditService.logIfChanged("Entity", "1", "field", "oldValue", "newValue", "user@test.com");
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogIfChanged_WhenValuesSame_ShouldNotCallLogChange() {
        auditService.logIfChanged("Entity", "1", "field", "same", "same", "user@test.com");
        verify(auditLogRepository, never()).save(any());
    }

    @Test
    void testExportTodayChangesAndDownload_WithContent() throws IOException {
        AuditLog log1 = AuditLog.builder().entityType("Entity").entityId("1").operation("CREATE")
                .fieldName("field").oldValue("old").newValue("new").userId("user@test.com")
                .timestamp(LocalDateTime.now()).details("details").build();

        when(auditLogRepository.findByTimestampBetween(any(), any()))
                .thenReturn(List.of(log1));

        ResponseEntity<Resource> response = auditService.exportTodayChangesAndDownload();

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contentLength() > 0);
    }

    @Test
    void testExportTodayChangesAndDownload_NoContent() {
        when(auditLogRepository.findByTimestampBetween(any(), any())).thenReturn(List.of());

        ResponseEntity<Resource> response = auditService.exportTodayChangesAndDownload();

        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
        assertNull(response.getBody());
    }
}
