package br.com.eventmanager.application.service.validation.publish;

import br.com.eventmanager.adapter.inbound.rest.exception.BusinessException;
import br.com.eventmanager.adapter.outbound.persistence.CategoryRepository;
import br.com.eventmanager.application.service.MessageService;
import br.com.eventmanager.application.service.validation.publish.rules.CategoryCodeValidationRule;
import br.com.eventmanager.domain.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static br.com.eventmanager.shared.Constants.EM_0009;
import static br.com.eventmanager.shared.Constants.EM_0011;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryCodeValidationRuleTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private CategoryCodeValidationRule categoryCodeValidationRule;

    private Event event;

    @BeforeEach
    void setup() {
        event = Event.builder()
                .id("evento teste")
                .title("teste")
                .description("Descrip teste")
                .categoryCode("CAT01")
                .locationCode("LOC01")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .maxCapacity(100)
                .price(20.0)
                .build();
    }

    @Test
    void givenNullCategoryCode_whenValidate_thenThrowBusinessException() {
        event.setCategoryCode(null);
        when(messageService.getMessage(EM_0011, null)).thenReturn("Category is required");

        assertThatThrownBy(() -> categoryCodeValidationRule.validate(event))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Category is required");
    }

    @Test
    void givenNonExistentCategoryCode_whenValidate_thenThrowBusinessException() {
        when(categoryRepository.existsByCategoryCode("CAT01")).thenReturn(false);
        when(messageService.getMessage(EM_0009, "CAT01")).thenReturn("Category does not exist");

        assertThatThrownBy(() -> categoryCodeValidationRule.validate(event))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Category does not exist");
    }

    @Test
    void givenValidCategoryCode_whenValidate_thenPasses() {
        when(categoryRepository.existsByCategoryCode("CAT01")).thenReturn(true);

        categoryCodeValidationRule.validate(event);
    }
}
