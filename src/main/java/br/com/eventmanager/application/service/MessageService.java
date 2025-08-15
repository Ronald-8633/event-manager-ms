package br.com.eventmanager.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageSource messageSource;

    public String getMessage(String code, String... args) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        var message = this.messageSource.getMessage(code, args, currentLocale);
        message = message.replaceAll("\"","");
        return message;
    }
}
