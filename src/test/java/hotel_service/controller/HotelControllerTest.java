package hotel_service.controller;

import hotel_service.dto.AddressDto;
import hotel_service.dto.ArrivalTimeDto;
import hotel_service.dto.ContactsDto;
import hotel_service.dto.CreateHotelRequest;
import hotel_service.dto.HotelDetailsDto;
import hotel_service.dto.HotelShortDto;
import hotel_service.exception.HotelNotFoundException;
import hotel_service.repository.HotelSearchCriteria;
import hotel_service.service.HotelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HotelController.class)
class HotelControllerTest {

    private static final String VALID_REQUEST = """
            {
              "name": "DoubleTree by Hilton Minsk",
              "description": "desc",
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

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HotelService hotelService;

    private HotelShortDto shortDto() {
        return new HotelShortDto(1L, "DoubleTree by Hilton Minsk", "desc",
                "9 Pobediteley Avenue, Minsk, 220004, Belarus", "+375 17 309-80-00");
    }

    private HotelDetailsDto detailsDto() {
        return new HotelDetailsDto(1L, "DoubleTree by Hilton Minsk", "desc", "Hilton",
                new AddressDto(9, "Pobediteley Avenue", "Minsk", "Belarus", "220004"),
                new ContactsDto("+375 17 309-80-00", "doubletreeminsk.info@hilton.com"),
                new ArrivalTimeDto("14:00", "12:00"),
                List.of("Free WiFi", "Concierge"));
    }

    @Test
    void getAll_returnsShortInfo() throws Exception {
        when(hotelService.getAll()).thenReturn(List.of(shortDto()));

        mockMvc.perform(get("/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("DoubleTree by Hilton Minsk"))
                .andExpect(jsonPath("$[0].address").value("9 Pobediteley Avenue, Minsk, 220004, Belarus"))
                .andExpect(jsonPath("$[0].phone").value("+375 17 309-80-00"));
    }

    @Test
    void getById_returnsExtendedInfo() throws Exception {
        when(hotelService.getById(1L)).thenReturn(detailsDto());

        mockMvc.perform(get("/hotels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Hilton"))
                .andExpect(jsonPath("$.address.houseNumber").value(9))
                .andExpect(jsonPath("$.address.city").value("Minsk"))
                .andExpect(jsonPath("$.contacts.email").value("doubletreeminsk.info@hilton.com"))
                .andExpect(jsonPath("$.arrivalTime.checkIn").value("14:00"))
                .andExpect(jsonPath("$.amenities[0]").value("Free WiFi"));
    }

    @Test
    void getById_returns404WhenHotelMissing() throws Exception {
        when(hotelService.getById(999L)).thenThrow(new HotelNotFoundException(999L));

        mockMvc.perform(get("/hotels/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Hotel with id 999 not found"));
    }

    @Test
    void getById_returns400ForNonNumericId() throws Exception {
        mockMvc.perform(get("/hotels/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid value for parameter 'id'"));
    }

    @Test
    void search_buildsCriteriaFromQueryParams() throws Exception {
        when(hotelService.search(new HotelSearchCriteria(null, null, "minsk", null, null)))
                .thenReturn(List.of(shortDto()));

        mockMvc.perform(get("/search").param("city", "minsk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void search_splitsAmenitiesByComma() throws Exception {
        when(hotelService.search(new HotelSearchCriteria(null, null, null, null,
                List.of("Free WiFi", "Free parking"))))
                .thenReturn(List.of(shortDto()));

        mockMvc.perform(get("/search").param("amenities", "Free WiFi,Free parking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void create_returns201WithLocationAndShortInfo() throws Exception {
        when(hotelService.create(any(CreateHotelRequest.class))).thenReturn(shortDto());

        mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/hotels/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("9 Pobediteley Avenue, Minsk, 220004, Belarus"));
    }

    @Test
    void create_returns400WhenNameBlank() throws Exception {
        String body = VALID_REQUEST.replace("\"name\": \"DoubleTree by Hilton Minsk\"", "\"name\": \"\"");

        mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("name")));
        verifyNoInteractions(hotelService);
    }

    @Test
    void create_returns400ForInvalidCheckInTime() throws Exception {
        String body = VALID_REQUEST.replace("14:00", "25:00");

        mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("arrivalTime.checkIn")));
        verifyNoInteractions(hotelService);
    }

    @Test
    void create_returns400ForMalformedJson() throws Exception {
        mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed JSON request"));
    }

    @Test
    void addAmenities_returnsUpdatedHotel() throws Exception {
        when(hotelService.addAmenities(1L, List.of("Free WiFi", "Concierge"))).thenReturn(detailsDto());

        mockMvc.perform(post("/hotels/1/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"Free WiFi\", \"Concierge\"]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amenities.length()").value(2));
    }

    @Test
    void addAmenities_returns400ForEmptyList() throws Exception {
        mockMvc.perform(post("/hotels/1/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(hotelService);
    }

    @Test
    void addAmenities_returns400ForBlankItem() throws Exception {
        mockMvc.perform(post("/hotels/1/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"Free WiFi\", \" \"]"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(hotelService);
    }

    @Test
    void addAmenities_returns404WhenHotelMissing() throws Exception {
        when(hotelService.addAmenities(999L, List.of("Free WiFi")))
                .thenThrow(new HotelNotFoundException(999L));

        mockMvc.perform(post("/hotels/999/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"Free WiFi\"]"))
                .andExpect(status().isNotFound());
    }

    @Test
    void unknownPath_returns404InApiErrorFormat() throws Exception {
        mockMvc.perform(get("/no-such-endpoint"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}