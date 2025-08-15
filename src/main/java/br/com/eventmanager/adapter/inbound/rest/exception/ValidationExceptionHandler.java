package br.com.eventmanager.adapter.inbound.rest.exception;

import br.com.eventmanager.domain.dto.ErrorDTO;
import br.com.eventmanager.domain.dto.ErrorDetailDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;

@RestControllerAdvice
public class ValidationExceptionHandler {

    public static final String ERROR_MESSAGE = "Erro de Validação";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var errorDTO = ErrorDTO.builder()
                .details(new ArrayList<>(0))
                .message(ERROR_MESSAGE)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();

        errorDTO.getDetails().addAll(
                ex.getBindingResult().getFieldErrors().stream()
                        .map(error -> {
                            return ErrorDetailDTO.builder()
                                    .description(error.getDefaultMessage())
                                    .item(error.getField())
                                    .build();
                        })
                        .toList()
        );
        return ResponseEntity.badRequest().body(errorDTO);


    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(BusinessException ex) {
        var errorDTO = ErrorDTO.builder()
                .details(new ArrayList<>(0))
                .message(ex.getMessage())
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .build();

        return ResponseEntity.unprocessableEntity().body(errorDTO);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(AccessDeniedException ex) {
        var errorDTO = ErrorDTO.builder()
                .details(new ArrayList<>(0))
                .message(ex.getMessage())
                .statusCode(HttpStatus.FORBIDDEN.value())
                .build();

        return ResponseEntity.unprocessableEntity().body(errorDTO);
    }
}
