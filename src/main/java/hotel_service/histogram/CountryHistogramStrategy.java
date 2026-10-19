package hotel_service.histogram;

import hotel_service.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CountryHistogramStrategy implements HistogramStrategy {

    private final HotelRepository hotelRepository;

    @Override
    public String param() {
        return "country";
    }

    @Override
    public Map<String, Long> build() {
        return hotelRepository.countByCountry();
    }
}