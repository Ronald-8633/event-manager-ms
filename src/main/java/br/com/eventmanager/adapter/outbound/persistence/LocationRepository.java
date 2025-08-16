package br.com.eventmanager.adapter.outbound.persistence;

import br.com.eventmanager.domain.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends MongoRepository<Location, String> {
    
    Optional<Location> findByLocationCode(String name);

    List<Location> findByIsActiveTrue();
    
    List<Location> findByCity(String city);
    
    List<Location> findByCapacityGreaterThanEqual(Integer capacity);

    Boolean existsByLocationCode(String code);
}
