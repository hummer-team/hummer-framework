package com.hummer.user.plugin.holder;

import com.hummer.common.exceptions.AppException;
import com.hummer.user.plugin.user.UserContext;

import javax.validation.constraints.NotNull;

public class UserHolder {
    private static final ThreadLocal<UserContext> LOCAL = new ThreadLocal<>();

    private UserHolder() {

    }

    public static UserContext get() {

        UserContext context = LOCAL.get();
        if (context == null) {
            throw new AppException(50005, "this current user is null,please login");
        }
        return context;
    }

    public static String getTrueName() {
        return get().getTrueName();
    }

    public static String getNickName() {
        return get().getNickName();
    }

    public static String getUserId() {
        return get().getUserId();
    }

    public static void set(@NotNull UserContext userContext) {
        LOCAL.set(userContext);
    }

    public static void clean() {
        LOCAL.remove();
    }
}