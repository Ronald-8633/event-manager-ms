package br.com.eventmanager.adapter.inbound.rest.audit;

import br.com.eventmanager.application.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuditController implements AuditApi {

    private final AuditService auditService;

    @Override
    public ResponseEntity<Resource> exportTodayChangesAndDownload() {
        return auditService.exportTodayChangesAndDownload();
    }
}
