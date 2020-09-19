package com.carlsberg.cx.notification.web.validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

  private static final String E164 = "^\\+(?:[0-9] ?){6,14}[0-9]$";

  @Override
  public void initialize(PhoneNumber phoneNumber) {}

  public boolean isPhoneValid(final String phoneNumber) {

    return phoneNumber == null || (phoneNumber != null && phoneNumber.matches(E164));
  }

  @Override
  public boolean isValid(String phoneField, ConstraintValidatorContext cxt) {

    return isPhoneValid(phoneField);

  }
}
