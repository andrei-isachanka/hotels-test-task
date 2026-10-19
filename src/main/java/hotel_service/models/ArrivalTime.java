package hotel_service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ArrivalTime {

    @Column(name = "check_in", nullable = false, length = 5)
    private String checkIn;

    @Column(name = "check_out", length = 5)
    private String checkOut;
}