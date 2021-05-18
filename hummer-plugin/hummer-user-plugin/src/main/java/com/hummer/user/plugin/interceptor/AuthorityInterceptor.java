package com.hummer.user.plugin.interceptor;

import com.hummer.common.exceptions.AppException;
import com.hummer.common.utils.AppBusinessAssert;
import com.hummer.core.PropertiesContainer;
import com.hummer.user.plugin.agent.AuthorityServiceAgent;
import com.hummer.user.plugin.annotation.AuthorityConditionEnum;
import com.hummer.user.plugin.annotation.NeedAuthority;
import com.hummer.user.plugin.annotation.member.MemberNeedAuthority;
import com.hummer.user.plugin.holder.ReqContextHolderProxy;
import com.hummer.user.plugin.holder.UserHolder;
import com.hummer.user.plugin.user.UserContext;
import com.hummer.user.plugin.user.member.MemberUserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

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
        //  管理用户校验
        verifyManageUserAuthority(request, handlerMethod);
        //  会员用户校验
        verifyMemberUserAuthority(request, handlerMethod);
        return true;
    }

    private void verifyMemberUserAuthority(HttpServletRequest request, HandlerMethod handler) {
        MemberNeedAuthority login = handler.getMethod().getAnnotation(MemberNeedAuthority.class);
        if (login == null) {
            login = handler.getClass().getAnnotation(MemberNeedAuthority.class);
            if (login == null) {
                return;
            }
        }
        String userId = ReqContextHolderProxy.MAP_HOLDER.get(
                PropertiesContainer.valueOfString("member.id.request.key", "memberId"));
        if (StringUtils.isEmpty(userId)) {

            String tokenKey = PropertiesContainer.valueOfString("ticket.request.key", "token");
            String token = ReqContextHolderProxy.MAP_HOLDER.get(tokenKey);
            if (StringUtils.isEmpty(token)) {
                throw new AppException(41001, "this request ticket not exists.");
            }
            userId = AuthorityServiceAgent.getMemberUserContext(token);
        }
        if (StringUtils.isEmpty(userId)) {
            log.debug("this request url {} ,controller method is {},ticket invalid"
                    , request.getRequestURI()
                    , ((HandlerMethod) handler).getMethod().getName());
            throw new AppException(41001, "this request ticket invalid.");
        }
        MemberUserContext userContext = AuthorityServiceAgent.queryUserNameByMemberId(userId);
        AppBusinessAssert.isTrue(userContext != null && userContext.getUserName() != null, 41001
                , String.format("user not exist,userId==%s", userId));
        userContext.setUserId(userId);
        UserHolder.setMember(userContext);
        log.info("method {} by userInfo userId=={}", handler.getMethod().getName(), userContext.getUserId());

    }


    private void verifyManageUserAuthority(HttpServletRequest request, HandlerMethod handler) {
        NeedAuthority login = handler.getMethod().getAnnotation(NeedAuthority.class);
        if (login == null) {
            login = handler.getClass().getAnnotation(NeedAuthority.class);
            if (login == null) {
                return;
            }
        }

        String tokenKey = PropertiesContainer.valueOfString("ticket.request.key", "token");
        String token = ReqContextHolderProxy.MAP_HOLDER.get(tokenKey);
        if (StringUtils.isEmpty(token)) {
            throw new AppException(41001, "this request ticket not exists.");
        }

        UserContext userContext = AuthorityServiceAgent.getUserContext(token);
        if (userContext == null) {
            log.debug("this request url {} ,controller method is {},ticket invalid"
                    , request.getRequestURI()
                    , ((HandlerMethod) handler).getMethod().getName());
            throw new AppException(41003, "this request ticket invalid.");
        }

        log.info("method {} by userInfo userId=={},userName=={}",
                ((HandlerMethod) handler).getMethod().getName(), userContext.getUserId(), userContext.getTrueName());

        if (Boolean.TRUE.equals(userContext.getIsLocked())) {
            log.warn("this user {} is locked,can't any operation", userContext.getTrueName());
            throw new AppException(40003, String.format("this user %s current status is locked."
                    , userContext.getTrueName()));
        }
        UserHolder.set(userContext);

        boolean disableAuthority = PropertiesContainer.valueOf("disable.authority", Boolean.class, Boolean.FALSE);
        if (disableAuthority) {
            return;
        }

        //if this user is supper admin then allow all operation
        if (Boolean.TRUE.equals(userContext.getIsSuperAdmin())) {
            UserHolder.set(userContext);
            return;
        }

        if (ArrayUtils.isEmpty(login.authorityCode())) {
            UserHolder.set(userContext);
            return;
        }

        if (login.authorityCondition() == AuthorityConditionEnum.ANY_OF) {
            for (String op : login.authorityCode()) {
                if (userContext.getAuthority()
                        .stream()
                        .map(m -> m.getAuthorityCode())
                        .anyMatch(f -> f.equalsIgnoreCase(op))) {
                    UserHolder.set(userContext);
                    return;
                }
            }
        } else {
            boolean allMatch = Arrays.stream(login.authorityCode()).allMatch(a ->
                    userContext
                            .getAuthority()
                            .stream()
                            .map(m -> m.getAuthorityCode())
                            .anyMatch(f -> f.equalsIgnoreCase(a)));
            if (allMatch) {
                UserHolder.set(userContext);
                return;
            }
        }

        throw new AppException(40100,
                String.format("this user %s,operation no authority,operation code is %s,remark is %s"
                        , userContext.getTrueName()
                        , Arrays.toString(login.authorityCode()), login.remark())
        );
    }
}
