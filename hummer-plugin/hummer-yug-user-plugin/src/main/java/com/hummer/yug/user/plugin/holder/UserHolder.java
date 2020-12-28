package com.hummer.yug.user.plugin.holder;

import com.hummer.common.exceptions.AppException;
import com.hummer.yug.user.plugin.dto.response.ShopInfoRespDto;
import com.hummer.yug.user.plugin.user.UserContext;

import javax.validation.constraints.NotNull;

public class UserHolder {
    private static final ThreadLocal<UserContext> LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<ShopInfoRespDto> SHOP_INFO = new ThreadLocal<>();

    private UserHolder() {

    }

    public static UserContext get() {

        UserContext context = LOCAL.get();
        if (context == null) {
            throw new AppException(50005, "this current user is null,please login");
        }
        return context;
    }

    public static Long getUserId() {

        return get().getYgfUserId();
    }


    public static void set(@NotNull UserContext userContext) {
        LOCAL.set(userContext);
    }

    public static void clean() {
        LOCAL.remove();
        SHOP_INFO.remove();
    }


    public static void setShop(@NotNull ShopInfoRespDto shopInfo) {
        SHOP_INFO.set(shopInfo);
    }

    public static ShopInfoRespDto getShop() {

        ShopInfoRespDto shopInfo = SHOP_INFO.get();
        if (shopInfo == null) {
            throw new AppException(40101, "this current shop is null,please login");
        }
        return shopInfo;
    }
}
