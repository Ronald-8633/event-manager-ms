package br.com.eventmanager.adapter.inbound.rest.auth;

import br.com.eventmanager.domain.dto.AuthResponseDTO;
import br.com.eventmanager.domain.dto.LoginRequestDTO;
import br.com.eventmanager.domain.dto.RegisterRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
public interface AuthApi {


    @Operation(summary = "Registro de usuário",
            description = "Registra um usuário e sua Role na aplicação"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description ="Usuário Registrado com sucesso",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @PostMapping("/register")
    ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request);


    @Operation(summary = "Loga um usuário",
            description = "Login Feito com sucesso"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description ="Usuário Logado com sucesso",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @PostMapping("/login")
    ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request);
}
