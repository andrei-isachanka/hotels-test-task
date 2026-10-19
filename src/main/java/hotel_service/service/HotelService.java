package hotel_service.service;

import hotel_service.dto.CreateHotelRequest;
import hotel_service.dto.HotelDetailsDto;
import hotel_service.dto.HotelShortDto;
import hotel_service.exception.HotelNotFoundException;
import hotel_service.mapper.HotelMapper;
import hotel_service.models.Amenity;
import hotel_service.models.Hotel;
import hotel_service.repository.AmenityRepository;
import hotel_service.repository.HotelRepository;
import hotel_service.repository.HotelSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final HotelMapper mapper;

    public List<HotelShortDto> getAll() {
        return hotelRepository.findAll().stream()
                .map(mapper::toShortDto)
                .toList();
    }

    public HotelDetailsDto getById(Long id) {
        return mapper.toDetailsDto(findOrThrow(id));
    }

    @Transactional
    public HotelShortDto create(CreateHotelRequest request) {
        Hotel saved = hotelRepository.save(mapper.toEntity(request));
        return mapper.toShortDto(saved);
    }

    @Transactional
    public HotelDetailsDto addAmenities(Long hotelId, List<String> amenities) {
        Hotel hotel = findOrThrow(hotelId);

        Set<String> requested = amenities.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<String, Amenity> resolved = new LinkedHashMap<>();
        amenityRepository.findAllByNames(requested)
                .forEach(a -> resolved.put(a.getName(), a));

        List<Amenity> missing = requested.stream()
                .filter(name -> !resolved.containsKey(name))
                .map(Amenity::new)
                .toList();
        amenityRepository.saveAll(missing)
                .forEach(a -> resolved.put(a.getName(), a));

        requested.forEach(name -> hotel.addAmenity(resolved.get(name)));
        hotelRepository.save(hotel);
        return mapper.toDetailsDto(hotel);
    }

    public List<HotelShortDto> search(HotelSearchCriteria criteria) {
        return hotelRepository.search(criteria).stream()
                .map(mapper::toShortDto)
                .toList();
    }

    private Hotel findOrThrow(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
    }
}