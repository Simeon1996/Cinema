package system.cinema.validator;

import system.cinema.annotation.ContactNumberConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ContactNumberValidator implements ConstraintValidator<ContactNumberConstraint, String> {
    @Override
    public boolean isValid(String contactField, ConstraintValidatorContext context) {
        // @TODO Optimize it.
        return
            contactField != null &&
            !contactField.isEmpty() &&
            contactField.matches("\\+[1-9]{1}[0-9]{3,14}");
    }
}
