package com.hummer.user.plugin.user;

import lombok.Data;

import java.util.List;

@Data
public class UserContext {
    private String userId;
    private String nickName;
    private String trueName;
    private String mobilePhone;
    private String mail;
    private Boolean isLocked;
    private Boolean isSupperAdmin;

    private List<RoleContext> role;
    private List<AuthorityContext> authority;
}
