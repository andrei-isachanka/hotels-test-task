package hotel_service.controller;

import hotel_service.dto.CreateHotelRequest;
import hotel_service.dto.HotelDetailsDto;
import hotel_service.dto.HotelShortDto;
import hotel_service.repository.HotelSearchCriteria;
import hotel_service.service.HotelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping("/hotels")
    public List<HotelShortDto> getAll() {
        return hotelService.getAll();
    }

    @GetMapping("/hotels/{id}")
    public HotelDetailsDto getById(@PathVariable Long id) {
        return hotelService.getById(id);
    }

    @GetMapping("/search")
    public List<HotelShortDto> search(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) String brand,
                                      @RequestParam(required = false) String city,
                                      @RequestParam(required = false) String country,
                                      @RequestParam(required = false) List<String> amenities) {
        return hotelService.search(new HotelSearchCriteria(name, brand, city, country, amenities));
    }

    @PostMapping("/hotels")
    public ResponseEntity<HotelShortDto> create(@Valid @RequestBody CreateHotelRequest request) {
        HotelShortDto created = hotelService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PostMapping("/hotels/{id}/amenities")
    public HotelDetailsDto addAmenities(@PathVariable Long id,
                                        @RequestBody @NotEmpty List<@NotBlank String> amenities) {
        return hotelService.addAmenities(id, amenities);
    }
}