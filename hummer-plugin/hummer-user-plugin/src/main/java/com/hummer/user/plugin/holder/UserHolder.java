package com.hummer.user.plugin.holder;

import com.hummer.common.exceptions.AppException;
import com.hummer.user.plugin.user.UserContext;
import com.hummer.user.plugin.user.member.MemberUserContext;

import javax.validation.constraints.NotNull;

public class UserHolder {
    private static final ThreadLocal<UserContext> LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<MemberUserContext> MEMBER_LOCAL = new ThreadLocal<>();

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
        MEMBER_LOCAL.remove();
    }

    public static MemberUserContext getMemberByAssertNull() {
        MemberUserContext context = MEMBER_LOCAL.get();
        if (context == null) {
            throw new AppException(40001, "this current user is null,please login");
        }
        return context;
    }

    public static void setMember(@NotNull MemberUserContext userContext) {
        MEMBER_LOCAL.set(userContext);
    }

}
