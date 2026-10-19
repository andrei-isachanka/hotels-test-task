package hotel_service.controller;

import hotel_service.exception.UnknownHistogramParamException;
import hotel_service.service.HistogramService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HistogramController.class)
class HistogramControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HistogramService histogramService;

    @Test
    void histogram_returnsCountsGroupedByParam() throws Exception {
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("Minsk", 1L);
        counts.put("Moscow", 2L);
        when(histogramService.build("city")).thenReturn(counts);

        mockMvc.perform(get("/histogram/city"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Minsk").value(1))
                .andExpect(jsonPath("$.Moscow").value(2));
    }

    @Test
    void histogram_returns400ForUnknownParam() throws Exception {
        when(histogramService.build("foo"))
                .thenThrow(new UnknownHistogramParamException("foo", Set.of("brand", "city")));

        mockMvc.perform(get("/histogram/foo"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("foo")));
    }
}