package hotel_service.histogram;

import hotel_service.repository.HotelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistogramStrategiesTest {

    @Mock
    private HotelRepository hotelRepository;

    @Test
    void brandStrategy_usesCountByBrand() {
        when(hotelRepository.countByBrand()).thenReturn(Map.of("Hilton", 2L));
        HistogramStrategy strategy = new BrandHistogramStrategy(hotelRepository);

        assertThat(strategy.param()).isEqualTo("brand");
        assertThat(strategy.build()).containsEntry("Hilton", 2L);
    }

    @Test
    void cityStrategy_usesCountByCity() {
        when(hotelRepository.countByCity()).thenReturn(Map.of("Minsk", 1L));
        HistogramStrategy strategy = new CityHistogramStrategy(hotelRepository);

        assertThat(strategy.param()).isEqualTo("city");
        assertThat(strategy.build()).containsEntry("Minsk", 1L);
    }

    @Test
    void countryStrategy_usesCountByCountry() {
        when(hotelRepository.countByCountry()).thenReturn(Map.of("Belarus", 3L));
        HistogramStrategy strategy = new CountryHistogramStrategy(hotelRepository);

        assertThat(strategy.param()).isEqualTo("country");
        assertThat(strategy.build()).containsEntry("Belarus", 3L);
    }

    @Test
    void amenitiesStrategy_usesCountByAmenity() {
        when(hotelRepository.countByAmenity()).thenReturn(Map.of("Free WiFi", 20L));
        HistogramStrategy strategy = new AmenitiesHistogramStrategy(hotelRepository);

        assertThat(strategy.param()).isEqualTo("amenities");
        assertThat(strategy.build()).containsEntry("Free WiFi", 20L);
    }
}