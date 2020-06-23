package com.hummer.user.plugin.holder;

import com.hummer.user.plugin.user.UserContext;

import javax.validation.constraints.NotNull;

public class UserHolder {
    private static final ThreadLocal<UserContext> LOCAL = new ThreadLocal<>();

    private UserHolder() {

    }

    public static UserContext get() {
        return LOCAL.get();
    }

    public static void set(@NotNull UserContext userContext) {
        LOCAL.set(userContext);
    }

    public static void clean() {
        LOCAL.remove();
    }
}
