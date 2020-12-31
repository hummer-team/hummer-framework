package com.hummer.yug.user.plugin.interceptor;

import com.hummer.common.utils.AppBusinessAssert;
import com.hummer.core.PropertiesContainer;
import com.hummer.yug.tools.plugin.enums.UserEnums;
import com.hummer.yug.user.plugin.agent.AuthorityServiceAgent;
import com.hummer.yug.user.plugin.annotation.member.MemberNeedAuthority;
import com.hummer.yug.user.plugin.dto.response.ShopInfoRespDto;
import com.hummer.yug.user.plugin.holder.RequestContextHolder;
import com.hummer.yug.user.plugin.holder.UserHolder;
import com.hummer.yug.user.plugin.user.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

/**
 * this class verify current user for  ticket , set user context info
 *
 * @author edz
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    /**
     * Intercept the execution of a handler. Called after HandlerMapping determined
     * an appropriate handler object, but before HandlerAdapter invokes the handler.
     * <p>DispatcherServlet processes a handler in an execution chain, consisting
     * of any number of interceptors, with the handler itself at the end.
     * With this method, each interceptor can decide to abort the execution chain,
     * typically sending an HTTP error or writing a custom response.
     * <p><strong>Note:</strong> special considerations apply for asynchronous
     * request processing. For more details see
     * {@link HandlerInterceptor}.
     * <p>The default implementation returns {@code true}.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute, for type and/or instance evaluation
     * @return {@code true} if the execution chain should proceed with the
     * next interceptor or the handler itself. Else, DispatcherServlet assumes
     * that this interceptor has already dealt with the response itself.
     * @throws Exception in case of errors
     */
    @Override
    public boolean preHandle(HttpServletRequest request
            , HttpServletResponse response
            , Object handler)
            throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //  会员用户校验
        verifyMemberAuthority(handlerMethod);
        //  管理用户校验
        verifyShopManagerAuthority(handlerMethod);
        return true;
    }

    private void verifyShopClientHeaders() {
        String headerKeys = PropertiesContainer.valueOfString("ticket.request.notnull.keys");
        if (StringUtils.isEmpty(headerKeys)) {
            return;
        }
        String[] keys = headerKeys.split(",");
        for (String key : keys) {
            if (StringUtils.isEmpty(key)) {
                continue;
            }
            AppBusinessAssert.isTrue(!StringUtils.isEmpty(RequestContextHolder.get(key))
                    , 41001, String.format("this request ticket %s not exists.", key));
        }
    }

    private String getShopClientCode() {
        String clientCodeKey = PropertiesContainer.valueOfString("ticket.request.shop.client.code", "clientCode");
        return RequestContextHolder.get(clientCodeKey);
    }

    private <T extends Annotation> T getAnnotation(HandlerMethod handler, Class<T> tClass) {

        T t = handler.getMethod().getAnnotation(tClass);
        if (t == null) {
            t = handler.getBeanType().getAnnotation(tClass);
        }
        return t;
    }


    private void verifyMemberAuthority(HandlerMethod handler) {
        MemberNeedAuthority login = getAnnotation(handler, MemberNeedAuthority.class);
        if (login == null) {
            return;
        }
        verifyShopClientHeaders();
        if (login.userType() != UserEnums.UserType.MEMBER && login.userType() != UserEnums.UserType.SHOP_MANAGER) {
            return;
        }

        String sidKey = PropertiesContainer.valueOfString("ticket.request.member.code", "sid");
        String userTokenKey = PropertiesContainer.valueOfString("ticket.request.member.token", "userToken");
        String sid = RequestContextHolder.get(sidKey);
        String userToken = RequestContextHolder.get(userTokenKey);

        AppBusinessAssert.isTrue(!StringUtils.isEmpty(sid), 41001, "this request ticket sid not exists.");
        AppBusinessAssert.isTrue(!StringUtils.isEmpty(userToken), 41001, "this request ticket userToken not exists.");
        UserContext userContext = AuthorityServiceAgent.queryUserContext(sid, userToken);
        AppBusinessAssert.isTrue(userContext != null, 41001, "user not exist");
        UserHolder.set(userContext);
        log.info("method {} by userInfo userId=={}", handler.getMethod().getName(), userContext.getYgfUserId());

    }

    private void verifyShopManagerAuthority(HandlerMethod handler) {
        MemberNeedAuthority login = getAnnotation(handler, MemberNeedAuthority.class);
        if (login == null) {
            return;
        }
        if (login.userType() != UserEnums.UserType.SHOP_MANAGER) {
            return;
        }

        String shopCodeKey = PropertiesContainer.valueOfString("ticket.request.shop.code", "shopCode");
        String shopCode = RequestContextHolder.get(shopCodeKey);
        AppBusinessAssert.isTrue(!StringUtils.isEmpty(shopCode), 41001, "this request ticket shopCode not exists.");

        ShopInfoRespDto shopInfo = AuthorityServiceAgent.queryShopByManagerAssert(UserHolder.getUserId(), shopCode);
        UserHolder.setShop(shopInfo);
    }
}