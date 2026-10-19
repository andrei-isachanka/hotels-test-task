package hotel_service.dto;

import java.util.List;

public record HotelDetailsDto(
        Long id,
        String name,
        String description,
        String brand,
        AddressDto address,
        ContactsDto contacts,
        ArrivalTimeDto arrivalTime,
        List<String> amenities) {
}