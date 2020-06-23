package com.hummer.user.plugin.user;

import lombok.Data;

@Data
public class AuthorityContext {
    private Integer id;
    private String authorityName;
    private String authorityCode;
    private String menuPath;
    private String menuName;
    private Integer menuId;
    private Integer authorityType;
}
