package com.hummer.framework.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Tolerate;

import java.io.Serializable;

/**
 * Necessary for proper Swagger documentation.
 *
 * @author romih
 */
@SuppressWarnings("unused")
@Getter
public class CustomErrorResponse implements Serializable {
    @Tolerate
    public CustomErrorResponse(){

    }

    public CustomErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    private static final long serialVersionUID = -7755563009111273632L;

    private String errorCode;

    private String errorMessage;

}
