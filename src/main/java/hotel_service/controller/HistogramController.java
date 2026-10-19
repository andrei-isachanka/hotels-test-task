package hotel_service.controller;

import hotel_service.service.HistogramService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HistogramController {

    private final HistogramService histogramService;

    @GetMapping("/histogram/{param}")
    public Map<String, Long> histogram(@PathVariable String param) {
        return histogramService.build(param);
    }
}