package hotel_service.controller;

import hotel_service.dto.CreateHotelRequest;
import hotel_service.dto.HotelDetailsDto;
import hotel_service.dto.HotelShortDto;
import hotel_service.exception.ApiError;
import hotel_service.repository.HotelSearchCriteria;
import hotel_service.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Hotels", description = "Hotel management and search")
public class HotelController {

    private static final String CREATE_HOTEL_EXAMPLE = """
            {
              "name": "DoubleTree by Hilton Minsk",
              "description": "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms ...",
              "brand": "Hilton",
              "address": {
                "houseNumber": 9,
                "street": "Pobediteley Avenue",
                "city": "Minsk",
                "country": "Belarus",
                "postCode": "220004"
              },
              "contacts": {
                "phone": "+375 17 309-80-00",
                "email": "doubletreeminsk.info@hilton.com"
              },
              "arrivalTime": {
                "checkIn": "14:00",
                "checkOut": "12:00"
              }
            }
            """;

    private static final String AMENITIES_EXAMPLE = """
            ["Free parking", "Free WiFi", "Non-smoking rooms", "Concierge", "On-site restaurant",
             "Fitness center", "Pet-friendly rooms", "Room service", "Business center", "Meeting rooms"]
            """;

    private final HotelService hotelService;

    @Operation(summary = "List all hotels", description = "Returns brief information about every hotel")
    @GetMapping("/hotels")
    public List<HotelShortDto> getAll() {
        return hotelService.getAll();
    }

    @Operation(summary = "Get hotel details", description = "Returns extended information about a hotel")
    @ApiResponse(responseCode = "200", description = "Hotel found")
    @ApiResponse(responseCode = "404", description = "Hotel not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/hotels/{id}")
    public HotelDetailsDto getById(@Parameter(description = "Hotel id", example = "1") @PathVariable Long id) {
        return hotelService.getById(id);
    }

    @Operation(summary = "Search hotels",
            description = "All parameters are optional and combined with AND. "
                    + "brand, city and country match exactly ignoring case; name matches a substring; "
                    + "a hotel must have all listed amenities.")
    @GetMapping("/search")
    public List<HotelShortDto> search(
            @Parameter(description = "Part of the hotel name", example = "hilton")
            @RequestParam(required = false) String name,
            @Parameter(description = "Brand", example = "Hilton")
            @RequestParam(required = false) String brand,
            @Parameter(description = "City", example = "minsk")
            @RequestParam(required = false) String city,
            @Parameter(description = "Country", example = "Belarus")
            @RequestParam(required = false) String country,
            @Parameter(description = "Comma-separated amenities", example = "Free WiFi,Free parking")
            @RequestParam(required = false) List<String> amenities) {
        return hotelService.search(new HotelSearchCriteria(name, brand, city, country, amenities));
    }

    @Operation(summary = "Create hotel",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = CREATE_HOTEL_EXAMPLE))))
    @ApiResponse(responseCode = "201", description = "Hotel created")
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PostMapping("/hotels")
    public ResponseEntity<HotelShortDto> create(@Valid @RequestBody CreateHotelRequest request) {
        HotelShortDto created = hotelService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Add amenities to hotel",
            description = "Adds amenities to the existing ones; duplicates are ignored. Returns the updated hotel.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = AMENITIES_EXAMPLE))))
    @ApiResponse(responseCode = "200", description = "Amenities added")
    @ApiResponse(responseCode = "400", description = "Empty list or blank item",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Hotel not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PostMapping("/hotels/{id}/amenities")
    public HotelDetailsDto addAmenities(@Parameter(description = "Hotel id", example = "1") @PathVariable Long id,
                                        @RequestBody @NotEmpty List<@NotBlank String> amenities) {
        return hotelService.addAmenities(id, amenities);
    }
}