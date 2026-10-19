package hotel_service.exception;

public class HotelNotFoundException extends RuntimeException {

    public HotelNotFoundException(Long id) {
        super("Hotel with id %d not found".formatted(id));
    }
}