package system.cinema.annotation;


import system.cinema.validator.EndTimeBiggerThanStartTimeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EndTimeBiggerThanStartTimeValidator.class)
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EndTimeBiggerThanStartTimeConstraint {
    String message() default "EndTime field must be bigger than StartTime and has at least a minimum duration between them.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
