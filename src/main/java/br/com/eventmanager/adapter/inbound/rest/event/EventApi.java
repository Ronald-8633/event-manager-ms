package br.com.eventmanager.adapter.inbound.rest.event;

import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.dto.AttendeeResponseDTO;
import br.com.eventmanager.domain.dto.EventDTO;
import br.com.eventmanager.domain.dto.EventRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Events", description = "Event management APIs")
@RequestMapping("/api/v1/events")
public interface EventApi {

    @Operation(summary = "Criação de eventos",
    description = "Api para criar eventos a partir de um request com detalhes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description ="Evento criado com sucesso",
                    content = @Content(schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @PostMapping
    ResponseEntity<Event> createEvent(@RequestBody @Valid EventRequestDTO event);

    @Operation(summary = "Busca Evento",
            description = "Captura um evento pela id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @Parameter(
            name = "id",
            description = "Id do evento",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/{id}")
    ResponseEntity<EventDTO> getEventById(@NotBlank @PathVariable(value = "id") String id);

    @Operation(summary = "Busca Eventos",
            description = "Lista todos os eventos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @GetMapping
    ResponseEntity<List<EventDTO>> getAllEvents();

    @Operation(summary = "Busca Eventos por categoria",
            description = "Lista eventos a partir de uma categoria selecionada"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @Parameter(
            name = "category",
            description = "Categoria do evento",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/category/{category}")
    ResponseEntity<List<EventDTO>> getEventsByCategory(@NotBlank @PathVariable(value = "category") String category);

    @Operation(summary = "Busca Eventos por status",
            description = "Lista eventos a partir de um status selecionada"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @Parameter(
            name = "status",
            description = "Status do evento",
            required = true,
            in = ParameterIn.PATH
    )
    @GetMapping("/status/{status}")
    ResponseEntity<List<EventDTO>> getEventsByStatus(@PathVariable(value = "status") Event.EventStatus status);

    @Operation(summary = "Atualização de eventos",
            description = "Atualiza um evento a partir de detalhes e id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @Parameter(
            name = "id",
            description = "id do evento",
            required = true,
            in = ParameterIn.PATH
    )
    @PutMapping("/{id}")
    ResponseEntity<Event> updateEvent(@PathVariable(value = "id") String id,
                                      @RequestBody EventRequestDTO eventDetails);

    @Operation(summary = "Exclusão de evento",
            description = "Exclui um evento a partir de uma id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description ="Requisição feita com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @Parameter(
            name = "id",
            description = "id do evento",
            required = true,
            in = ParameterIn.PATH
    )
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteEvent(@NotBlank @PathVariable(value = "id") String id);

    @Operation(summary = "Publica eventos",
            description = "Publicação de eventos a partir de uma id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @Parameter(
            name = "id",
            description = "id do evento",
            required = true,
            in = ParameterIn.PATH
    )
    @PostMapping("/{id}/publish")
    ResponseEntity<Event> publishEvent(@PathVariable(value = "id") String id);

    @Operation(summary = "Adição de atendentes",
            description = "Adiciona um atendente a um evento a partir da id do usuario"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description ="Requisição feita com sucesso",
                    content = @Content(schema = @Schema(implementation = AttendeeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não Autorizado"),
    })
    @Parameter(
            name = "id",
            description = "id do evento",
            required = true,
            in = ParameterIn.PATH
    )
    @Parameter(
            name = "userId",
            description = "id do usuario",
            required = true,
            in = ParameterIn.PATH
    )
    @PostMapping("/{id}/attendees/{userId}")
    ResponseEntity<AttendeeResponseDTO> addAttendee(@NotBlank @PathVariable(value = "id") String id,
                                                    @NotBlank @PathVariable(value = "userId") String userId);
}
