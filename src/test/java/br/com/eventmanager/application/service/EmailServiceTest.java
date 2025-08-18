package br.com.eventmanager.application.service;

import br.com.eventmanager.domain.dto.EmailRequestDTO;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.MessageRejectedException;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private AmazonSimpleEmailService sesClient;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "teste@gmail.com");
    }

    private EmailRequestDTO buildEmailRequest() {
        return EmailRequestDTO.builder()
                .toEmail("teste1@gmail.com")
                .eventTitle("Teste Evento")
                .eventDate("20/08/2025")
                .eventTime("10:00")
                .eventLocation("São Paulo")
                .organizerName("Organizador Teste")
                .confirmationCode("ABC123")
                .build();
    }

    @Test
    void shouldSendEmailSuccessfully() {
        EmailRequestDTO request = buildEmailRequest();

        emailService.sendEventConfirmation(request);

        verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
    }

    @Test
    void shouldHandleMessageRejectedException() {
        EmailRequestDTO request = buildEmailRequest();
        doThrow(MessageRejectedException.class).when(sesClient).sendEmail(any(SendEmailRequest.class));

        emailService.sendEventConfirmation(request);

        verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
    }

    @Test
    void shouldHandleGenericException() {
        EmailRequestDTO request = buildEmailRequest();
        doThrow(RuntimeException.class).when(sesClient).sendEmail(any(SendEmailRequest.class));

        emailService.sendEventConfirmation(request);

        verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
    }

    @Test
    void shouldBuildCorrectSendEmailRequest() {
        EmailRequestDTO request = buildEmailRequest();

        var argumentCaptor = ArgumentCaptor.forClass(SendEmailRequest.class);

        emailService.sendEventConfirmation(request);

        verify(sesClient).sendEmail(argumentCaptor.capture());
        SendEmailRequest sentRequest = argumentCaptor.getValue();

        assert sentRequest.getSource().equals("teste@gmail.com");

        assert sentRequest.getDestination().getToAddresses().contains("teste1@gmail.com");

        assert sentRequest.getMessage().getSubject().getData().contains(request.getEventTitle());

        assert sentRequest.getMessage().getBody().getHtml().getData().contains(request.getConfirmationCode());
    }
}
