package hotel_service.repository;

import hotel_service.models.Amenity;

import java.util.Collection;
import java.util.List;

public interface AmenityRepository {

    List<Amenity> findAllByNames(Collection<String> names);

    List<Amenity> saveAll(Collection<Amenity> amenities);
}