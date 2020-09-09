package system.cinema.validator;

import system.cinema.annotation.ValidDateValueConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Date;
import java.time.LocalDate;

public class ValidDateValueValidator implements ConstraintValidator<ValidDateValueConstraint, Date> {

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext context) {
        if (date == null) {
            return false;
        }

        LocalDate yesterday = LocalDate.now().minusDays(1);

        return date.toLocalDate().isAfter(yesterday);
    }
}