package hotel_service.integration;

import com.jayway.jsonpath.JsonPath;
import hotel_service.repository.jpa.AmenityJpaRepository;
import hotel_service.repository.jpa.HotelJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HotelApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HotelJpaRepository hotelJpa;

    @Autowired
    private AmenityJpaRepository amenityJpa;

    @Autowired
    private Environment environment;

    @BeforeEach
    void cleanDatabase() {
        hotelJpa.deleteAll();
        amenityJpa.deleteAll();
    }

    @Test
    void application_usesPortAndPrefixFromRequirements() {
        assertThat(environment.getProperty("server.port")).isEqualTo("8092");
        assertThat(environment.getProperty("server.servlet.context-path")).isEqualTo("/property-view");
    }

    @Test
    void fullScenario_createAddAmenitiesGetSearchAndHistogram() throws Exception {
        long minskHilton = createHotel(hotelJson("Hilton Minsk", "Hilton", "Minsk", "Belarus"));
        long moscowHilton = createHotel(hotelJson("Hilton Moscow", "Hilton", "Moscow", "Russia"));
        createHotel(hotelJson("Marriott Minsk", "Marriott", "Minsk", "Belarus"));

        addAmenities(minskHilton, "[\"Free WiFi\", \"Free parking\"]");
        addAmenities(moscowHilton, "[\"Free WiFi\"]");
        addAmenities(minskHilton, "[\"Free WiFi\", \"Concierge\"]");

        mockMvc.perform(get("/hotels/" + minskHilton))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hilton Minsk"))
                .andExpect(jsonPath("$.address.city").value("Minsk"))
                .andExpect(jsonPath("$.arrivalTime.checkIn").value("14:00"))
                .andExpect(jsonPath("$.amenities.length()").value(3));

        mockMvc.perform(get("/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].address").isString());

        mockMvc.perform(get("/search").param("city", "minsk"))
                .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(get("/search").param("brand", "hilton").param("amenities", "Free WiFi"))
                .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(get("/search").param("amenities", "Concierge"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Hilton Minsk"));
        mockMvc.perform(get("/search").param("city", "Paris"))
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/histogram/city"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Minsk").value(2))
                .andExpect(jsonPath("$.Moscow").value(1));
        mockMvc.perform(get("/histogram/brand"))
                .andExpect(jsonPath("$.Hilton").value(2))
                .andExpect(jsonPath("$.Marriott").value(1));
        mockMvc.perform(get("/histogram/country"))
                .andExpect(jsonPath("$.Belarus").value(2))
                .andExpect(jsonPath("$.Russia").value(1));
        mockMvc.perform(get("/histogram/amenities"))
                .andExpect(jsonPath("$['Free WiFi']").value(2))
                .andExpect(jsonPath("$['Free parking']").value(1))
                .andExpect(jsonPath("$.Concierge").value(1));
    }

    @Test
    void errors_areReturnedInUnifiedFormat() throws Exception {
        mockMvc.perform(get("/hotels/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        mockMvc.perform(get("/histogram/foo"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private long createHotel(String body) throws Exception {
        String response = mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    private void addAmenities(long hotelId, String body) throws Exception {
        mockMvc.perform(post("/hotels/" + hotelId + "/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    private String hotelJson(String name, String brand, String city, String country) {
        return """
                {
                  "name": "%s",
                  "brand": "%s",
                  "address": {
                    "houseNumber": 1,
                    "street": "Main street",
                    "city": "%s",
                    "country": "%s",
                    "postCode": "000000"
                  },
                  "contacts": {"phone": "+1 000", "email": "info@example.com"},
                  "arrivalTime": {"checkIn": "14:00"}
                }
                """.formatted(name, brand, city, country);
    }
}