package hotel_service.repository.jpa;

import hotel_service.models.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HotelJpaRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {

    @Query("select h.brand, count(h) from Hotel h group by h.brand order by h.brand")
    List<Object[]> countGroupedByBrand();

    @Query("select h.address.city, count(h) from Hotel h group by h.address.city order by h.address.city")
    List<Object[]> countGroupedByCity();

    @Query("select h.address.country, count(h) from Hotel h group by h.address.country order by h.address.country")
    List<Object[]> countGroupedByCountry();

    @Query("select a.name, count(h) from Hotel h join h.amenities a group by a.name order by a.name")
    List<Object[]> countGroupedByAmenity();
}