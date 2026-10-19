package hotel_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ContactsDto(
        @NotBlank String phone,
        @NotBlank @Email String email) {
}