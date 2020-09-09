package system.cinema.annotation;

import system.cinema.validator.ContactNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ContactNameValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ContactNameConstraint {
    String message() default "The identity must be composed of at least first and last name.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
