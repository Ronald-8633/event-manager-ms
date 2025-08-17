package br.com.eventmanager.adapter.inbound.rest.location;

import br.com.eventmanager.adapter.outbound.persistence.LocationRepository;
import br.com.eventmanager.domain.Location;
import br.com.eventmanager.domain.dto.LocationDTO;
import br.com.eventmanager.domain.mapper.EventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LocationControllerTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private LocationController locationController;

    private Location location;
    private LocationDTO locationDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        location = Location.builder()
                .id("1")
                .name("Auditório Central")
                .locationCode("AUD001")
                .address("Rua Principal, 100")
                .city("São Paulo")
                .state("SP")
                .country("Brasil")
                .capacity(200)
                .description("Espaço para eventos corporativos")
                .imageUrl("http://image.com/auditorio.png")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        locationDTO = LocationDTO.builder()
                .name(location.getName())
                .locationCode(location.getLocationCode())
                .address(location.getAddress())
                .city(location.getCity())
                .state(location.getState())
                .country(location.getCountry())
                .capacity(location.getCapacity())
                .description(location.getDescription())
                .imageUrl(location.getImageUrl())
                .isActive(location.getIsActive())
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                .build();
    }

    @Test
    void shouldReturnAllLocations() {
        when(locationRepository.findByIsActiveTrue()).thenReturn(List.of(location));
        when(eventMapper.toLocationDTO(location)).thenReturn(locationDTO);

        ResponseEntity<List<LocationDTO>> response = locationController.getAllLocations();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).containsExactly(locationDTO);
        verify(locationRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void shouldReturnLocationByIdWhenFound() {
        when(locationRepository.findById("1")).thenReturn(Optional.of(location));
        when(eventMapper.toLocationDTO(location)).thenReturn(locationDTO);

        ResponseEntity<LocationDTO> response = locationController.getLocationById("1");

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(locationDTO);
        verify(locationRepository, times(1)).findById("1");
    }

    @Test
    void shouldReturnNotFoundWhenLocationByIdDoesNotExist() {
        when(locationRepository.findById("99")).thenReturn(Optional.empty());

        ResponseEntity<LocationDTO> response = locationController.getLocationById("99");

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody()).isNull();
        verify(locationRepository, times(1)).findById("99");
    }

    @Test
    void shouldReturnLocationsByCity() {
        when(locationRepository.findByCity("São Paulo")).thenReturn(List.of(location));
        when(eventMapper.toLocationDTO(location)).thenReturn(locationDTO);

        ResponseEntity<List<LocationDTO>> response = locationController.getLocationsByCity("São Paulo");

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).containsExactly(locationDTO);
        verify(locationRepository, times(1)).findByCity("São Paulo");
    }

    @Test
    void shouldReturnEmptyListWhenCityHasNoLocations() {
        when(locationRepository.findByCity("Rio")).thenReturn(Collections.emptyList());

        ResponseEntity<List<LocationDTO>> response = locationController.getLocationsByCity("Rio");

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEmpty();
        verify(locationRepository, times(1)).findByCity("Rio");
    }

    @Test
    void shouldReturnLocationsByMinCapacity() {
        when(locationRepository.findByCapacityGreaterThanEqual(150)).thenReturn(Arrays.asList(location));
        when(eventMapper.toLocationDTO(location)).thenReturn(locationDTO);

        ResponseEntity<List<LocationDTO>> response = locationController.getLocationsByMinCapacity(150);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).containsExactly(locationDTO);
        verify(locationRepository, times(1)).findByCapacityGreaterThanEqual(150);
    }
}
