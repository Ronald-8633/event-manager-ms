package br.com.eventmanager.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDTO {

    private int statusCode;
    private String message;
    private List<ErrorDetailDTO> details;
}
