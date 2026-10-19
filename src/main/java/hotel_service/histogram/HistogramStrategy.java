package hotel_service.histogram;

import java.util.Map;

public interface HistogramStrategy {

    String param();

    Map<String, Long> build();
}