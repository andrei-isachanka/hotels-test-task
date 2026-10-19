package hotel_service;

import hotel_service.dto.AddressDto;
import hotel_service.dto.ArrivalTimeDto;
import hotel_service.dto.ContactsDto;
import hotel_service.dto.CreateHotelRequest;
import hotel_service.models.Address;
import hotel_service.models.ArrivalTime;
import hotel_service.models.Contacts;
import hotel_service.models.Hotel;

public final class TestData {

    public static final String DESCRIPTION =
            "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms in the Belorussian capital ...";

    private TestData() {
    }

    public static CreateHotelRequest createRequest() {
        return new CreateHotelRequest(
                "DoubleTree by Hilton Minsk",
                DESCRIPTION,
                "Hilton",
                new AddressDto(9, "Pobediteley Avenue", "Minsk", "Belarus", "220004"),
                new ContactsDto("+375 17 309-80-00", "doubletreeminsk.info@hilton.com"),
                new ArrivalTimeDto("14:00", "12:00"));
    }

    public static Hotel hotel(Long id) {
        return Hotel.builder()
                .id(id)
                .name("DoubleTree by Hilton Minsk")
                .description(DESCRIPTION)
                .brand("Hilton")
                .address(new Address(9, "Pobediteley Avenue", "Minsk", "Belarus", "220004"))
                .contacts(new Contacts("+375 17 309-80-00", "doubletreeminsk.info@hilton.com"))
                .arrivalTime(new ArrivalTime("14:00", "12:00"))
                .build();
    }
}