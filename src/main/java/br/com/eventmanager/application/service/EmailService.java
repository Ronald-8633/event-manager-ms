package br.com.eventmanager.application.service;

import br.com.eventmanager.domain.dto.EmailRequestDTO;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final AmazonSimpleEmailService sesClient;

    @Value("${aws.ses.from-email}")
    private String fromEmail;


    private static final String SUBJECT_PREFIX = "Confirmação de Inscrição - ";

    public void sendEventConfirmation(EmailRequestDTO emailRequest) {
        try {
            var request = buildEmailRequest(emailRequest);
            sesClient.sendEmail(request);

            log.info("Email de confirmação enviado com sucesso para: {}", emailRequest.getToEmail());
        } catch (MessageRejectedException e) {
            log.error("Email rejeitado pelo SES (endereço inválido ou bloqueado): {}", emailRequest.getToEmail(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao enviar email de confirmação via AWS SES para: {}", emailRequest.getToEmail(), e);
        }
    }

    private SendEmailRequest buildEmailRequest(EmailRequestDTO emailRequest) {
        return new SendEmailRequest()
                .withSource(fromEmail)
                .withDestination(new Destination().withToAddresses(emailRequest.getToEmail()))
                .withMessage(new Message()
                        .withSubject(new Content(SUBJECT_PREFIX + emailRequest.getEventTitle()))
                        .withBody(new Body().withHtml(buildEmailBody(emailRequest))));
    }

    private Content buildEmailBody(EmailRequestDTO request) {
        String htmlBody = String.format("""
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Confirmação de Inscrição</title>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #667eea, #764ba2);
                                  color: white; padding: 30px; text-align: center;
                                  border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .event-details { background: white; padding: 20px; margin: 20px 0;
                                         border-radius: 8px; border-left: 4px solid #667eea; }
                        .confirmation-code { background: #e8f5e8; padding: 15px; border-radius: 8px;
                                             text-align: center; font-size: 18px; font-weight: bold; color: #2d5a2d; }
                        .footer { text-align: center; margin-top: 30px; color: #666; font-size: 14px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎉 Inscrição Confirmada!</h1>
                            <p>Parabéns! Sua inscrição foi realizada com sucesso.</p>
                        </div>
                        
                        <div class="content">
                            <h2>Detalhes do Evento</h2>
                            <div class="event-details">
                                <h3>%s</h3>
                                <p><strong>📅 Data:</strong> %s</p>
                                <p><strong>⏰ Horário:</strong> %s</p>
                                <p><strong>📍 Local:</strong> %s</p>
                                <p><strong>👤 Organizador:</strong> %s</p>
                            </div>
                            
                            <div class="confirmation-code">
                                <p>Código de Confirmação:</p>
                                <p>%s</p>
                            </div>
                            
                            <p><strong>Importante:</strong></p>
                            <ul>
                                <li>Chegue com 15 minutos de antecedência</li>
                                <li>Leve um documento com foto</li>
                                <li>Este código será solicitado na entrada</li>
                            </ul>
                            
                            <p>Em caso de dúvidas, entre em contato conosco.</p>
                        </div>
                        
                        <div class="footer">
                            <p>Event Manager MS - Sistema de Gestão de Eventos</p>
                            <p>Este email foi enviado automaticamente, não responda.</p>
                        </div>
                    </div>
                </body>
                </html>
                """,
                request.getEventTitle(),
                request.getEventDate(),
                request.getEventTime(),
                request.getEventLocation(),
                request.getOrganizerName(),
                request.getConfirmationCode()
        );

        return new Content().withData(htmlBody);
    }
}
