package system.cinema.validator;

import system.cinema.annotation.ContactNameConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ContactNameValidator implements ConstraintValidator<ContactNameConstraint, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return
            value != null &&
            !value.isEmpty() &&
            value.matches("([a-zA-Z]+\\s?\\b){2,}");
    }
}
