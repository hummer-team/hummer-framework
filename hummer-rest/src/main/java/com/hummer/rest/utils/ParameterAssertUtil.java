package com.hummer.rest.utils;

import com.hummer.common.exceptions.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;

/**
 * spring controller request body pram assert
 * Created by liguo on 2017/9/20.
 */
public class ParameterAssertUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParameterAssertUtil.class);

    private ParameterAssertUtil() {

    }

    public static void assertRequestValidated(Errors errors, boolean getAll) {
        assertRequestValidated(4000, errors, getAll);
    }

    public static void assertRequestValidated(int errorCode, Errors errors, boolean getAll) {
        if (getAll) {
            assertRequestValidated(errors);
        } else {
            if (errors != null && errors.hasErrors()) {
                org.springframework.validation.FieldError fieldError = errors.getFieldError();
                throw new AppException(errorCode, fieldError.getDefaultMessage());
            }
        }
    }

    public static void assertRequestFristValidated(Errors errors) {
        assertRequestValidated(errors, false);
    }

    public static void assertRequestFristValidated(int errorCode, Errors errors) {

        assertRequestValidated(errorCode, errors, false);
    }

    public static void assertRequestValidated(Errors errors) {

        assertRequestValidated(4000, errors);
    }

    public static void assertRequestValidated(int errorCode, Errors errors) {
        if (errors != null && errors.hasErrors()) {

            StringBuilder stringBuilder = new StringBuilder();
            errors.getFieldErrors().forEach(e -> {
                stringBuilder.append(e.getDefaultMessage());
                stringBuilder.append(",");
            });
            throw new AppException(errorCode, String.format("%s", stringBuilder.toString()));
        }
    }
}
