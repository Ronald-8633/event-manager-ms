package br.com.eventmanager.adapter.inbound.rest.user;

import br.com.eventmanager.domain.dto.UserProfileDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Users", description = "User management APIs")
@RequestMapping("/api/v1/users")
public interface UserApi {

    @Operation(summary = "busca perfil do usuário logado",
            description = "Captura detalhes do perfil de um usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = UserProfileDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile();

    @Operation(summary = "Busca usuário",
            description = "Captura um usuário por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = UserProfileDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @Parameter(
            name = "id",
            description = "Id do usuário",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getUserById(@PathVariable String id);

    @Operation(summary = "Busca usuários",
            description = "Captura a lista de todos os usuários"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = UserProfileDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @GetMapping
    public ResponseEntity<List<UserProfileDTO>> getAllUsers();

    @Operation(summary = "Exclusão de usuário",
            description = "Exclui um usuário pela id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description ="Requisição feita com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @Parameter(
            name = "id",
            description = "Id do usuário",
            required = true,
            in = ParameterIn.PATH
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id);
}
