package hotel_service.controller;

import hotel_service.exception.ApiError;
import hotel_service.service.HistogramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Histogram", description = "Hotel counts grouped by a parameter")
public class HistogramController {

    private final HistogramService histogramService;

    @Operation(summary = "Hotel histogram",
            description = "Returns the number of hotels grouped by each value of the parameter")
    @ApiResponse(responseCode = "200", description = "Counts by value, e.g. {\"Minsk\": 1, \"Moscow\": 2}")
    @ApiResponse(responseCode = "400", description = "Unknown parameter",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/histogram/{param}")
    public Map<String, Long> histogram(
            @Parameter(description = "One of: brand, city, country, amenities", example = "city")
            @PathVariable String param) {
        return histogramService.build(param);
    }
}