package hotel_service.exception;

import java.util.Set;

public class UnknownHistogramParamException extends RuntimeException {

    public UnknownHistogramParamException(String param, Set<String> supported) {
        super("Unknown histogram parameter '%s'. Supported: %s".formatted(param, supported));
    }
}