package br.com.eventmanager.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.time.Duration;
import java.util.Locale;

@Configuration
public class CustomMessageHandlerInterceptor implements HandlerInterceptor {

    private static final Locale DEFAULT_LOCALE = new Locale.Builder().setLanguage("pt").setRegion("BR").build();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        LocaleContextHolder.setLocale(DEFAULT_LOCALE, true);
        final String localeParam = request.getParameter("lang");

        if (StringUtils.hasText(localeParam)) {
            LocaleContextHolder.setLocale(Locale.forLanguageTag(localeParam), true);
        }
        return true;
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        var cookieLocaleResolver = new CookieLocaleResolver("my-locale-cookie");
        cookieLocaleResolver.setDefaultLocale(DEFAULT_LOCALE);
        cookieLocaleResolver.setCookieMaxAge(Duration.ofSeconds(3600));
        return cookieLocaleResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeInterceptor() {
        var interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }
}
