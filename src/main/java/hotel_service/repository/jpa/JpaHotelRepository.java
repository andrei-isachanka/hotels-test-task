package hotel_service.repository.jpa;

import hotel_service.models.Hotel;
import hotel_service.repository.HotelRepository;
import hotel_service.repository.HotelSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaHotelRepository implements HotelRepository {

    private final HotelJpaRepository jpa;

    @Override
    public Hotel save(Hotel hotel) {
        return jpa.save(hotel);
    }

    @Override
    public Optional<Hotel> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Hotel> findAll() {
        return jpa.findAll();
    }

    @Override
    public List<Hotel> search(HotelSearchCriteria criteria) {
        return jpa.findAll(HotelSpecifications.byCriteria(criteria));
    }

    @Override
    public Map<String, Long> countByBrand() {
        return toMap(jpa.countGroupedByBrand());
    }

    @Override
    public Map<String, Long> countByCity() {
        return toMap(jpa.countGroupedByCity());
    }

    @Override
    public Map<String, Long> countByCountry() {
        return toMap(jpa.countGroupedByCountry());
    }

    @Override
    public Map<String, Long> countByAmenity() {
        return toMap(jpa.countGroupedByAmenity());
    }

    private Map<String, Long> toMap(List<Object[]> rows) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put((String) row[0], ((Number) row[1]).longValue());
        }
        return result;
    }
}