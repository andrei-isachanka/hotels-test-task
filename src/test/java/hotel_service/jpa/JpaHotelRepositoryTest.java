package hotel_service.repository.jpa;

import hotel_service.models.Address;
import hotel_service.models.Amenity;
import hotel_service.models.ArrivalTime;
import hotel_service.models.Contacts;
import hotel_service.models.Hotel;
import hotel_service.repository.HotelSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaHotelRepository.class)
class JpaHotelRepositoryTest {

    @Autowired
    private JpaHotelRepository repository;

    @Autowired
    private HotelJpaRepository hotelJpa;

    @Autowired
    private AmenityJpaRepository amenityJpa;

    @BeforeEach
    void setUp() {
        Amenity wifi = amenityJpa.save(new Amenity("Free WiFi"));
        Amenity parking = amenityJpa.save(new Amenity("Free parking"));
        Amenity fitness = amenityJpa.save(new Amenity("Fitness center"));

        saveHotel("Hilton Minsk", "Hilton", "Minsk", "Belarus", wifi, parking);
        saveHotel("Marriott Minsk", "Marriott", "Minsk", "Belarus", wifi);
        saveHotel("Hilton Moscow", "Hilton", "Moscow", "Russia", wifi, fitness, parking);
    }

    private Hotel saveHotel(String name, String brand, String city, String country, Amenity... amenities) {
        Hotel hotel = Hotel.builder()
                .name(name)
                .brand(brand)
                .address(new Address(1, "Main street", city, country, "000000"))
                .contacts(new Contacts("+1 000", "info@example.com"))
                .arrivalTime(new ArrivalTime("14:00", null))
                .build();
        for (Amenity amenity : amenities) {
            hotel.addAmenity(amenity);
        }
        return hotelJpa.save(hotel);
    }

    private List<String> searchNames(HotelSearchCriteria criteria) {
        return repository.search(criteria).stream().map(Hotel::getName).toList();
    }

    @Test
    void search_withoutCriteria_returnsAllHotels() {
        assertThat(searchNames(new HotelSearchCriteria(null, null, null, null, null)))
                .containsExactlyInAnyOrder("Hilton Minsk", "Marriott Minsk", "Hilton Moscow");
    }

    @Test
    void search_byCity_ignoresCase() {
        assertThat(searchNames(new HotelSearchCriteria(null, null, "minsk", null, null)))
                .containsExactlyInAnyOrder("Hilton Minsk", "Marriott Minsk");
    }

    @Test
    void search_byBrandAndCountry_combinesFilters() {
        assertThat(searchNames(new HotelSearchCriteria(null, "HILTON", null, "russia", null)))
                .containsExactly("Hilton Moscow");
    }

    @Test
    void search_byNamePart_matchesSubstring() {
        assertThat(searchNames(new HotelSearchCriteria("hilton", null, null, null, null)))
                .containsExactlyInAnyOrder("Hilton Minsk", "Hilton Moscow");
    }

    @Test
    void search_byAmenities_requiresAllOfThem() {
        assertThat(searchNames(new HotelSearchCriteria(null, null, null, null,
                List.of("free wifi", "FITNESS CENTER"))))
                .containsExactly("Hilton Moscow");
    }

    @Test
    void search_withNoMatches_returnsEmptyList() {
        assertThat(searchNames(new HotelSearchCriteria(null, null, "Paris", null, null))).isEmpty();
    }

    @Test
    void countByCity_groupsHotels() {
        assertThat(repository.countByCity()).isEqualTo(Map.of("Minsk", 2L, "Moscow", 1L));
    }

    @Test
    void countByBrand_groupsHotels() {
        assertThat(repository.countByBrand()).isEqualTo(Map.of("Hilton", 2L, "Marriott", 1L));
    }

    @Test
    void countByCountry_groupsHotels() {
        assertThat(repository.countByCountry()).isEqualTo(Map.of("Belarus", 2L, "Russia", 1L));
    }

    @Test
    void countByAmenity_countsEachHotelOncePerAmenity() {
        assertThat(repository.countByAmenity())
                .isEqualTo(Map.of("Free WiFi", 3L, "Free parking", 2L, "Fitness center", 1L));
    }

    @Test
    void save_persistsEmbeddedObjectsAndAmenities() {
        Hotel saved = hotelJpa.findAll().stream()
                .filter(h -> h.getName().equals("Hilton Minsk"))
                .findFirst()
                .orElseThrow();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAddress().getCity()).isEqualTo("Minsk");
        assertThat(saved.getContacts().getEmail()).isEqualTo("info@example.com");
        assertThat(saved.getArrivalTime().getCheckIn()).isEqualTo("14:00");
        assertThat(saved.getArrivalTime().getCheckOut()).isNull();
        assertThat(saved.getAmenities()).extracting(Amenity::getName)
                .containsExactlyInAnyOrder("Free WiFi", "Free parking");
    }
}