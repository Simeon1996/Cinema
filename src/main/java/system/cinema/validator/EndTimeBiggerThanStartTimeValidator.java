package system.cinema.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import system.cinema.EntitiesConfiguration;
import system.cinema.annotation.EndTimeBiggerThanStartTimeConstraint;
import system.cinema.model.Movie;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Time;

@EnableConfigurationProperties(EntitiesConfiguration.class)
public class EndTimeBiggerThanStartTimeValidator implements ConstraintValidator<EndTimeBiggerThanStartTimeConstraint, Movie> {

    @Value("${movie.minimum.duration.value}")
    private String minimumMovieDuration;

    @Autowired
    private ApplicationContext appContext;

    @Override
    public boolean isValid(Movie movie, ConstraintValidatorContext context) {
        if (movie.getStartTime() == null || movie.getEndTime() == null) {
            return false;
        }

        Time startTime = movie.getStartTime();
        Time endTime = movie.getEndTime();

        if (startTime.after(endTime)) {
            return false;
        }

        // @TODO This should be extracted into the variable with value taken from external props!
        return ((endTime.getTime() - startTime.getTime()) / 1000 / 60) > 40;
    }
}
