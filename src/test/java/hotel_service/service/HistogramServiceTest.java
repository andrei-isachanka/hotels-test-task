package hotel_service.service;

import hotel_service.exception.UnknownHistogramParamException;
import hotel_service.histogram.HistogramStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HistogramServiceTest {

    private HistogramStrategy strategy(String param) {
        HistogramStrategy strategy = mock(HistogramStrategy.class);
        when(strategy.param()).thenReturn(param);
        return strategy;
    }

    @Test
    void build_usesStrategyMatchingParam() {
        HistogramStrategy city = strategy("city");
        HistogramStrategy brand = strategy("brand");
        when(city.build()).thenReturn(Map.of("Minsk", 1L));
        HistogramService service = new HistogramService(List.of(city, brand));

        assertThat(service.build("city")).containsEntry("Minsk", 1L);
    }

    @Test
    void build_ignoresParamCase() {
        HistogramStrategy city = strategy("city");
        when(city.build()).thenReturn(Map.of("Minsk", 1L));
        HistogramService service = new HistogramService(List.of(city));

        assertThat(service.build("CITY")).containsEntry("Minsk", 1L);
    }

    @Test
    void build_throwsForUnknownParam() {
        HistogramService service = new HistogramService(List.of(strategy("city"), strategy("brand")));

        assertThatThrownBy(() -> service.build("foo"))
                .isInstanceOf(UnknownHistogramParamException.class)
                .hasMessageContaining("foo")
                .hasMessageContaining("brand")
                .hasMessageContaining("city");
    }
}