package br.com.eventmanager.adapter.inbound.rest.location;

import br.com.eventmanager.domain.dto.LocationDTO;
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

@RequestMapping("/api/v1/locations")
@Tag(name = "Locations", description = "Location management APIs")
public interface LocationApi {

    @Operation(summary = "Busca localizações",
            description = "Listagem de localizações"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = LocationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations();

    @Operation(summary = "Busca localização",
            description = "Busca uma localização pela id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = LocationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @Parameter(
            name = "id",
            description = "Id da localização",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable String id);

    @Operation(summary = "Busca localização",
            description = "Busca uma localização pela cidade"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = LocationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @Parameter(
            name = "city",
            description = "Cidade da localização",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/city/{city}")
    public ResponseEntity<List<LocationDTO>> getLocationsByCity(@PathVariable String city);

    @Operation(summary = "Busca localização",
            description = "Busca uma localização pela capacidade minima"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = LocationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @Parameter(
            name = "minCapacity",
            description = "Capacidade minima d alocalização",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/capacity/{minCapacity}")
    public ResponseEntity<List<LocationDTO>> getLocationsByMinCapacity(@PathVariable Integer minCapacity);
}
