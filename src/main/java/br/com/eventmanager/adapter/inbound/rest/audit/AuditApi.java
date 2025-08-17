package br.com.eventmanager.adapter.inbound.rest.audit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/audit")
@Tag(name = "Audit", description = "Audit management APIs")
public interface AuditApi {

    @Operation(summary = "Exportação de arquivos",
            description = "Exporta arquivos que foram modificados hoje para um csv"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description ="Exportação feita com sucesso",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @PostMapping("/export/today")
    ResponseEntity<Resource> exportTodayChangesAndDownload();
}
