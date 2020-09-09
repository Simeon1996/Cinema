package system.cinema;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
@PropertySource("classpath:entities.properties")
public class EntitiesConfiguration {

    @Value("${movie.minimum.duration.value}")
    private String movieMinDurationValue;

    public String getMovieMinDurationValue() {
        return movieMinDurationValue;
    }

    public void setMovieMinDurationValue(String value) {
        movieMinDurationValue = value;
    }
}
