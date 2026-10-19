package hotel_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ArrivalTimeDto(
        @NotBlank
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "must be in HH:mm format")
        String checkIn,

        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "must be in HH:mm format")
        String checkOut) {
}