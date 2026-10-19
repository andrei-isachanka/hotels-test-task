package hotel_service.mapper;

import hotel_service.TestData;
import hotel_service.dto.AddressDto;
import hotel_service.dto.ArrivalTimeDto;
import hotel_service.dto.ContactsDto;
import hotel_service.dto.CreateHotelRequest;
import hotel_service.dto.HotelDetailsDto;
import hotel_service.dto.HotelShortDto;
import hotel_service.models.Amenity;
import hotel_service.models.Hotel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HotelMapperTest {

    private final HotelMapper mapper = new HotelMapper();

    @Test
    void toShortDto_formatsAddressAsSingleLine() {
        HotelShortDto dto = mapper.toShortDto(TestData.hotel(1L));

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("DoubleTree by Hilton Minsk");
        assertThat(dto.address()).isEqualTo("9 Pobediteley Avenue, Minsk, 220004, Belarus");
        assertThat(dto.phone()).isEqualTo("+375 17 309-80-00");
    }

    @Test
    void toDetailsDto_mapsNestedObjectsAndAmenities() {
        Hotel hotel = TestData.hotel(1L);
        hotel.addAmenity(new Amenity("Free WiFi"));
        hotel.addAmenity(new Amenity("Concierge"));

        HotelDetailsDto dto = mapper.toDetailsDto(hotel);

        assertThat(dto.brand()).isEqualTo("Hilton");
        assertThat(dto.address().houseNumber()).isEqualTo(9);
        assertThat(dto.address().city()).isEqualTo("Minsk");
        assertThat(dto.contacts().email()).isEqualTo("doubletreeminsk.info@hilton.com");
        assertThat(dto.arrivalTime().checkIn()).isEqualTo("14:00");
        assertThat(dto.arrivalTime().checkOut()).isEqualTo("12:00");
        assertThat(dto.amenities()).containsExactlyInAnyOrder("Free WiFi", "Concierge");
    }

    @Test
    void toEntity_mapsAllFieldsAndLeavesIdEmpty() {
        Hotel hotel = mapper.toEntity(TestData.createRequest());

        assertThat(hotel.getId()).isNull();
        assertThat(hotel.getName()).isEqualTo("DoubleTree by Hilton Minsk");
        assertThat(hotel.getBrand()).isEqualTo("Hilton");
        assertThat(hotel.getAddress().getPostCode()).isEqualTo("220004");
        assertThat(hotel.getContacts().getPhone()).isEqualTo("+375 17 309-80-00");
        assertThat(hotel.getArrivalTime().getCheckIn()).isEqualTo("14:00");
        assertThat(hotel.getAmenities()).isEmpty();
    }

    @Test
    void toEntity_allowsMissingDescriptionAndCheckOut() {
        CreateHotelRequest request = new CreateHotelRequest(
                "Hotel", null, "Brand",
                new AddressDto(1, "Street", "City", "Country", "000"),
                new ContactsDto("+1", "a@b.com"),
                new ArrivalTimeDto("14:00", null));

        Hotel hotel = mapper.toEntity(request);

        assertThat(hotel.getDescription()).isNull();
        assertThat(hotel.getArrivalTime().getCheckOut()).isNull();
    }
}