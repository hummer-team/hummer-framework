package com.hummer.yug.user.plugin.holder;

import com.hummer.common.exceptions.AppException;
import com.hummer.common.utils.AppBusinessAssert;
import com.hummer.common.utils.CommonUtil;
import com.hummer.yug.tools.plugin.constants.Constants;
import com.hummer.yug.tools.plugin.util.SysEnums;
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

    public static Long getOperatorId() {

        return CommonUtil.typeChange(RequestContextHolder.get(Constants.HeadersKey.OPERATOR_ID), Long.class);
    }


    public static Long getOperatorIdAssertNull() {

        Long id = getOperatorId();
        AppBusinessAssert.isTrue(id != null, 40101, "operation need login");
        return id;
    }


    public static String getClientResource() {

        return RequestContextHolder.get(Constants.HeadersKey.CLIENT_RESOURCE);
    }

    public static SysEnums.ClientResourceEnum getClientResourceEnum() {

        return SysEnums.ClientResourceEnum.getEnumByValue(getClientResource());
    }
}
