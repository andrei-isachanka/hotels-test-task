package hotel_service.repository.jpa;

import hotel_service.models.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AmenityJpaRepository extends JpaRepository<Amenity, Long> {

    List<Amenity> findByNameIn(Collection<String> names);
}