package com.hummer.yug.user.plugin.holder;

import com.hummer.common.exceptions.AppException;
import com.hummer.yug.user.plugin.user.UserContext;

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
        return get().getUserName();
    }

    public static String getNickName() {
        return get().getNickName();
    }

    public static Long getUserId() {
        
        return get().getYgfUserId();
    }

    public static void set(@NotNull UserContext userContext) {
        LOCAL.set(userContext);
    }

    public static void clean() {
        LOCAL.remove();
    }

}
