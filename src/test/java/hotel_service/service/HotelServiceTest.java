package hotel_service.service;

import hotel_service.TestData;
import hotel_service.dto.HotelDetailsDto;
import hotel_service.dto.HotelShortDto;
import hotel_service.exception.HotelNotFoundException;
import hotel_service.mapper.HotelMapper;
import hotel_service.models.Amenity;
import hotel_service.models.Hotel;
import hotel_service.repository.AmenityRepository;
import hotel_service.repository.HotelRepository;
import hotel_service.repository.HotelSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private AmenityRepository amenityRepository;

    @Captor
    private ArgumentCaptor<Collection<Amenity>> amenitiesCaptor;

    private HotelService service;

    @BeforeEach
    void setUp() {
        service = new HotelService(hotelRepository, amenityRepository, new HotelMapper());
    }

    @Test
    void getAll_returnsShortDtos() {
        when(hotelRepository.findAll()).thenReturn(List.of(TestData.hotel(1L)));

        List<HotelShortDto> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).address()).isEqualTo("9 Pobediteley Avenue, Minsk, 220004, Belarus");
    }

    @Test
    void getById_returnsDetails() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(TestData.hotel(1L)));

        HotelDetailsDto result = service.getById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.brand()).isEqualTo("Hilton");
    }

    @Test
    void getById_throwsWhenHotelMissing() {
        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(999L))
                .isInstanceOf(HotelNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void create_savesMappedEntityAndReturnsShortDto() {
        when(hotelRepository.save(any(Hotel.class))).thenReturn(TestData.hotel(1L));

        HotelShortDto result = service.create(TestData.createRequest());

        ArgumentCaptor<Hotel> captor = ArgumentCaptor.forClass(Hotel.class);
        verify(hotelRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(captor.getValue().getName()).isEqualTo("DoubleTree by Hilton Minsk");
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void addAmenities_createsOnlyMissingAmenities() {
        Hotel hotel = TestData.hotel(1L);
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(amenityRepository.findAllByNames(any())).thenReturn(List.of(new Amenity("Free WiFi")));
        when(amenityRepository.saveAll(any()))
                .thenAnswer(inv -> new ArrayList<>(inv.<Collection<Amenity>>getArgument(0)));

        HotelDetailsDto result = service.addAmenities(1L, List.of("Free WiFi", "Concierge"));

        verify(amenityRepository).saveAll(amenitiesCaptor.capture());
        assertThat(amenitiesCaptor.getValue()).extracting(Amenity::getName).containsExactly("Concierge");
        assertThat(result.amenities()).containsExactlyInAnyOrder("Free WiFi", "Concierge");
    }

    @Test
    void addAmenities_trimsNamesAndIgnoresDuplicatesAndBlanks() {
        Hotel hotel = TestData.hotel(1L);
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(amenityRepository.findAllByNames(Set.of("Free WiFi"))).thenReturn(List.of());
        when(amenityRepository.saveAll(any()))
                .thenAnswer(inv -> new ArrayList<>(inv.<Collection<Amenity>>getArgument(0)));

        HotelDetailsDto result = service.addAmenities(1L, List.of("  Free WiFi ", "Free WiFi", " "));

        assertThat(result.amenities()).containsExactly("Free WiFi");
    }

    @Test
    void addAmenities_throwsWhenHotelMissing() {
        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addAmenities(999L, List.of("Free WiFi")))
                .isInstanceOf(HotelNotFoundException.class);
        verifyNoInteractions(amenityRepository);
    }

    @Test
    void search_delegatesCriteriaToRepository() {
        HotelSearchCriteria criteria = new HotelSearchCriteria(null, null, "minsk", null, null);
        when(hotelRepository.search(criteria)).thenReturn(List.of(TestData.hotel(1L)));

        List<HotelShortDto> result = service.search(criteria);

        assertThat(result).hasSize(1);
        verify(hotelRepository).search(criteria);
    }
}