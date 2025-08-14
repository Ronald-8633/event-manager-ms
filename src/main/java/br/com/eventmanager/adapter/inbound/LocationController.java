package br.com.eventmanager.adapter.inbound;

import br.com.eventmanager.adapter.outbound.persistence.LocationRepository;
import br.com.eventmanager.domain.Location;
import br.com.eventmanager.domain.dto.LocationDTO;
import br.com.eventmanager.domain.mapper.EventMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Tag(name = "Locations", description = "Location management APIs")
public class LocationController {
    
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;
    
    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        List<Location> locations = locationRepository.findByIsActiveTrue();
        List<LocationDTO> locationDTOs = locations.stream()
                .map(eventMapper::toLocationDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(locationDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable String id) {
        return locationRepository.findById(id)
                .map(eventMapper::toLocationDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<List<LocationDTO>> getLocationsByCity(@PathVariable String city) {
        List<Location> locations = locationRepository.findByCity(city);
        List<LocationDTO> locationDTOs = locations.stream()
                .map(eventMapper::toLocationDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(locationDTOs);
    }
    
    @GetMapping("/capacity/{minCapacity}")
    public ResponseEntity<List<LocationDTO>> getLocationsByMinCapacity(@PathVariable Integer minCapacity) {
        List<Location> locations = locationRepository.findByCapacityGreaterThanEqual(minCapacity);
        List<LocationDTO> locationDTOs = locations.stream()
                .map(eventMapper::toLocationDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(locationDTOs);
    }
}
