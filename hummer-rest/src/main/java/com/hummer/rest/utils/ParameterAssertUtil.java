package com.hummer.rest.utils;

import com.hummer.common.exceptions.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.function.Supplier;

/**
 * spring controller request body pram assert
 * Created by liguo on 2017/9/20.
 */
public class ParameterAssertUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParameterAssertUtil.class);

    private ParameterAssertUtil() {

    }

    public static void assertConditionTrue(boolean condition
            , Supplier<? extends AppException> exception) {
        if (!condition) {
            throw exception.get();
        }
    }

    public static void assertConditionFalse(boolean condition
            , Supplier<? extends AppException> exception) {
        if (condition) {
            throw exception.get();
        }
    }

    public static void assertRequestValidated(Errors errors, boolean getAll) {
        assertRequestValidated(40000, errors, getAll);
    }

    public static void assertRequestValidated(int errorCode, Errors errors, boolean getAll) {
        if (getAll) {
            assertRequestValidated(errors);
        } else {
            if (errors != null && errors.hasErrors()) {
                FieldError fieldError = errors.getFieldError();
                if (fieldError != null) {
                    throw new AppException(errorCode, fieldError.getDefaultMessage());
                } else {
                    throw new AppException(errorCode, "input parameter validation error。");
                }
            }
        }
    }

    public static void assertRequestFirstValidated(Errors errors) {
        assertRequestValidated(errors, false);
    }

    public static void assertRequestFirstValidated(int errorCode, Errors errors) {

        assertRequestValidated(errorCode, errors, false);
    }

    public static void assertRequestValidated(Errors errors) {

        assertRequestValidated(40000, errors);
    }

    public static void assertRequestValidated(int errorCode, Errors errors) {
        if (errors != null && errors.hasErrors()) {

            StringBuilder stringBuilder = new StringBuilder();
            errors.getFieldErrors().forEach(e -> {
                stringBuilder.append(e.getDefaultMessage());
                stringBuilder.append(",");
            });

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("assertRequestValidated has error is {}", stringBuilder.toString());
            }

            throw new AppException(errorCode, String.format("%s", stringBuilder.toString()));
        }
    }
}
