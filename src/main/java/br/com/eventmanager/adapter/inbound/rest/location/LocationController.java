package br.com.eventmanager.adapter.inbound.rest.location;

import br.com.eventmanager.adapter.outbound.persistence.LocationRepository;
import br.com.eventmanager.domain.Location;
import br.com.eventmanager.domain.dto.LocationDTO;
import br.com.eventmanager.domain.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class LocationController implements LocationApi {
    
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;
    
    @Override
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        List<Location> locations = locationRepository.findByIsActiveTrue();
        List<LocationDTO> locationDTOs = locations.stream()
                .map(eventMapper::toLocationDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(locationDTOs);
    }

    @Override
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable String id) {
        return locationRepository.findById(id)
                .map(eventMapper::toLocationDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<LocationDTO>> getLocationsByCity(@PathVariable String city) {
        List<Location> locations = locationRepository.findByCity(city);
        List<LocationDTO> locationDTOs = locations.stream()
                .map(eventMapper::toLocationDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(locationDTOs);
    }

    @Override
    public ResponseEntity<List<LocationDTO>> getLocationsByMinCapacity(@PathVariable Integer minCapacity) {
        List<Location> locations = locationRepository.findByCapacityGreaterThanEqual(minCapacity);
        List<LocationDTO> locationDTOs = locations.stream()
                .map(eventMapper::toLocationDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(locationDTOs);
    }
}
