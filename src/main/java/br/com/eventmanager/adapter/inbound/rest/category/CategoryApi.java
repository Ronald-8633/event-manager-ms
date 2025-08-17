package br.com.eventmanager.adapter.inbound.rest.category;

import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.dto.CategoryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Category management APIs")
public interface CategoryApi {

    @Operation(summary = "Busca categorias",
            description = "Busca todas as categorias"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories();

    @Operation(summary = "Busca categoria",
            description = "Busca uma categoria pela id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @Parameter(
            name = "id",
            description = "Id da categoria",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable String id);

    @Operation(summary = "Busca categoria",
            description = "Busca uma categoria pela id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @Parameter(
            name = "name",
            description = "Nome da categoria",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryDTO> getCategoryByName(@PathVariable String name);
}
