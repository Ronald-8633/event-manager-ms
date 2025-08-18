package br.com.eventmanager.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {

    private String toEmail;
    private String toName;
    private String eventTitle;
    private String eventDate;
    private String eventLocation;
    private String eventTime;
    private String organizerName;
    private String confirmationCode;
}
