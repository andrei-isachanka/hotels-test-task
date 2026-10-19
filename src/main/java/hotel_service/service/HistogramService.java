package hotel_service.service;

import hotel_service.exception.UnknownHistogramParamException;
import hotel_service.histogram.HistogramStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HistogramService {

    private final Map<String, HistogramStrategy> strategies;

    public HistogramService(List<HistogramStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(HistogramStrategy::param, Function.identity()));
    }

    public Map<String, Long> build(String param) {
        HistogramStrategy strategy = strategies.get(param.toLowerCase(Locale.ROOT));
        if (strategy == null) {
            throw new UnknownHistogramParamException(param, new TreeSet<>(strategies.keySet()));
        }
        return strategy.build();
    }
}