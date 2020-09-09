package system.cinema.annotation;

import system.cinema.validator.ValidDateValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidDateValueValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateValueConstraint {
    String message() default "Date is not valid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}