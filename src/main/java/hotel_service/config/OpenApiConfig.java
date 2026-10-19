package hotel_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hotelsOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Hotels API")
                .version("1.0.0")
                .description("RESTful API for hotels: list, details, search, creation, amenities and histograms"));
    }
}