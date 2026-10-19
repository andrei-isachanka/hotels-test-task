package hotel_service.repository.jpa;

import hotel_service.models.Amenity;
import hotel_service.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaAmenityRepository implements AmenityRepository {

    private final AmenityJpaRepository jpa;

    @Override
    public List<Amenity> findAllByNames(Collection<String> names) {
        return jpa.findByNameIn(names);
    }

    @Override
    public List<Amenity> saveAll(Collection<Amenity> amenities) {
        return jpa.saveAll(amenities);
    }
}