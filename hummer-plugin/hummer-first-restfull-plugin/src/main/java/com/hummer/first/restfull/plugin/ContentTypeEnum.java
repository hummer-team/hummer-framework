package com.hummer.first.restfull.plugin;

public enum ContentTypeEnum {
    JSON("application/json"),
    FORMDATA("multipart/form-data"),
    FORMURLENCODED("application/x-www-form-urlencoded")
    ;
    private String type;

    ContentTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
