package br.com.eventmanager.adapter.inbound.rest.audit;

import br.com.eventmanager.application.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditControllerTest {

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuditController controller;

    private Resource mockResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockResource = new ByteArrayResource("mock csv content".getBytes());
    }

    @Test
    void testExportTodayChangesAndDownload_WithContent() {
        when(auditService.exportTodayChangesAndDownload()).thenReturn(
                ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=\"audit.csv\"")
                        .body(mockResource)
        );

        ResponseEntity<Resource> response = controller.exportTodayChangesAndDownload();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(mockResource, response.getBody());

        verify(auditService, times(1)).exportTodayChangesAndDownload();
    }

    @Test
    void testExportTodayChangesAndDownload_NoContent() {
        when(auditService.exportTodayChangesAndDownload()).thenReturn(
                ResponseEntity.noContent().build()
        );

        ResponseEntity<Resource> response = controller.exportTodayChangesAndDownload();

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(auditService, times(1)).exportTodayChangesAndDownload();
    }
}
