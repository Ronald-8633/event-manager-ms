package br.com.eventmanager.domain.mapper;

import br.com.eventmanager.domain.Category;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.Location;
import br.com.eventmanager.domain.dto.CategoryDTO;
import br.com.eventmanager.domain.dto.EventRequestDTO;
import br.com.eventmanager.domain.dto.LocationDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {
    
    CategoryDTO toCategoryDTO(Category category);
    
    LocationDTO toLocationDTO(Location location);

    void toEvent(EventRequestDTO requestDTO, @MappingTarget Event event);
}
