package com.hummer.user.auth.plugin.context;

import lombok.Data;

import java.util.Map;

/**
 * @author chenwei
 */
@Data
public class UserContext {

    private String userId;

    private String userName;

    private Map<String, Object> data;
}
