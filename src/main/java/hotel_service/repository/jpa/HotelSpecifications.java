package hotel_service.repository.jpa;

import hotel_service.models.Hotel;
import hotel_service.repository.HotelSearchCriteria;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Objects;

final class HotelSpecifications {

    private HotelSpecifications() {
    }

    static Specification<Hotel> byCriteria(HotelSearchCriteria criteria) {
        Specification<Hotel> spec = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(criteria.name())) {
            spec = spec.and(nameContains(criteria.name()));
        }
        if (StringUtils.hasText(criteria.brand())) {
            spec = spec.and(brandEquals(criteria.brand()));
        }
        if (StringUtils.hasText(criteria.city())) {
            spec = spec.and(addressFieldEquals("city", criteria.city()));
        }
        if (StringUtils.hasText(criteria.country())) {
            spec = spec.and(addressFieldEquals("country", criteria.country()));
        }
        if (criteria.amenities() != null) {
            for (String amenity : criteria.amenities()) {
                if (StringUtils.hasText(amenity)) {
                    spec = spec.and(hasAmenity(amenity));
                }
            }
        }
        return spec;
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static Specification<Hotel> nameContains(String value) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.<String>get("name")), "%" + normalize(value) + "%");
    }

    private static Specification<Hotel> brandEquals(String value) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.<String>get("brand")), normalize(value));
    }

    private static Specification<Hotel> addressFieldEquals(String field, String value) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("address").<String>get(field)), normalize(value));
    }

    private static Specification<Hotel> hasAmenity(String amenity) {
        return (root, query, cb) -> {
            Subquery<Long> sub = Objects.requireNonNull(query).subquery(Long.class);
            Root<Hotel> subRoot = sub.from(Hotel.class);
            Join<Object, Object> join = subRoot.join("amenities");
            sub.select(subRoot.<Long>get("id")).where(
                    cb.equal(subRoot.<Long>get("id"), root.<Long>get("id")),
                    cb.equal(cb.lower(join.<String>get("name")), normalize(amenity)));
            return cb.exists(sub);
        };
    }
}