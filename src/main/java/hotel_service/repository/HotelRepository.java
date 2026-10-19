package hotel_service.repository;

import hotel_service.models.Hotel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HotelRepository {

    Hotel save(Hotel hotel);

    Optional<Hotel> findById(Long id);

    List<Hotel> findAll();

    List<Hotel> search(HotelSearchCriteria criteria);

    Map<String, Long> countByBrand();

    Map<String, Long> countByCity();

    Map<String, Long> countByCountry();

    Map<String, Long> countByAmenity();
}