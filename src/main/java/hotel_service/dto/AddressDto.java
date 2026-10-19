package hotel_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddressDto(
        @NotNull @Positive Integer houseNumber,
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank String country,
        @NotBlank String postCode) {
}