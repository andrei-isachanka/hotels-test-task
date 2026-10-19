package hotel_service.mapper;

import hotel_service.dto.AddressDto;
import hotel_service.dto.ArrivalTimeDto;
import hotel_service.dto.ContactsDto;
import hotel_service.dto.CreateHotelRequest;
import hotel_service.dto.HotelDetailsDto;
import hotel_service.dto.HotelShortDto;
import hotel_service.models.Address;
import hotel_service.models.Amenity;
import hotel_service.models.ArrivalTime;
import hotel_service.models.Contacts;
import hotel_service.models.Hotel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HotelMapper {

    public Hotel toEntity(CreateHotelRequest request) {
        AddressDto a = request.address();
        ContactsDto c = request.contacts();
        ArrivalTimeDto t = request.arrivalTime();

        return Hotel.builder()
                .name(request.name())
                .description(request.description())
                .brand(request.brand())
                .address(new Address(a.houseNumber(), a.street(), a.city(), a.country(), a.postCode()))
                .contacts(new Contacts(c.phone(), c.email()))
                .arrivalTime(new ArrivalTime(t.checkIn(), t.checkOut()))
                .build();
    }

    public HotelShortDto toShortDto(Hotel hotel) {
        return new HotelShortDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getDescription(),
                formatAddress(hotel.getAddress()),
                hotel.getContacts().getPhone());
    }

    public HotelDetailsDto toDetailsDto(Hotel hotel) {
        Address a = hotel.getAddress();
        Contacts c = hotel.getContacts();
        ArrivalTime t = hotel.getArrivalTime();
        List<String> amenities = hotel.getAmenities().stream().map(Amenity::getName).toList();

        return new HotelDetailsDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getDescription(),
                hotel.getBrand(),
                new AddressDto(a.getHouseNumber(), a.getStreet(), a.getCity(), a.getCountry(), a.getPostCode()),
                new ContactsDto(c.getPhone(), c.getEmail()),
                new ArrivalTimeDto(t.getCheckIn(), t.getCheckOut()),
                amenities);
    }

    private String formatAddress(Address a) {
        return "%d %s, %s, %s, %s".formatted(
                a.getHouseNumber(), a.getStreet(), a.getCity(), a.getPostCode(), a.getCountry());
    }
}