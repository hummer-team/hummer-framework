package com.hummer.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Data
public class QueryStringDto implements Serializable {
    private static final long serialVersionUID = 7152083639351568260L;

    @NotEmpty(message = "uuId can't null")
    private String uuId;
    @NotNull(message = "class id can't null")
    private Integer classId;
    private Collection<String> users;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date atTime;
}
