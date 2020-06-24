package com.hummer.user.plugin.interceptor;

import com.hummer.common.exceptions.AppException;
import com.hummer.core.PropertiesContainer;
import com.hummer.user.plugin.agent.AuthorityServiceAgent;
import com.hummer.user.plugin.annotation.AuthorityConditionEnum;
import com.hummer.user.plugin.annotation.NeedAuthority;
import com.hummer.user.plugin.holder.RequestContextHolder;
import com.hummer.user.plugin.holder.UserHolder;
import com.hummer.user.plugin.user.UserContext;
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
     * {@link AsyncHandlerInterceptor}.
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

        NeedAuthority login = ((HandlerMethod) handler).getMethod().getAnnotation(NeedAuthority.class);
        if (login == null) {
            login = handler.getClass().getAnnotation(NeedAuthority.class);
            if (login == null) {
                return true;
            }
        }


        String tokenKey = PropertiesContainer.valueOfString("ticket.request.key", "token");
        String token = RequestContextHolder.get(tokenKey);
        if (StringUtils.isEmpty(token)) {
            return false;
        }

        UserContext userContext = AuthorityServiceAgent.getUserContext(token);
        if (userContext == null) {
            log.debug("this request url {} ,controller method is {},ticket invalid"
                    , request.getRequestURI()
                    , ((HandlerMethod) handler).getMethod().getName());
            return false;
        }

        if (Boolean.TRUE.equals(userContext.getIsLocked())) {
            log.warn("this user {} is locked,can't any operation", userContext.getTrueName());
            throw new AppException(40001, String.format("this user %s current status is locked."
                    , userContext.getTrueName()));
        }

        //if this user is supper admin then allow all operation
        if (Boolean.TRUE.equals(userContext.getIsSupperAdmin())) {
            UserHolder.set(userContext);
            return true;
        }

        if (ArrayUtils.isEmpty(login.authorityCode())) {
            UserHolder.set(userContext);
            return true;
        }

        if (login.authorityCondition() == AuthorityConditionEnum.ANY_OF) {
            for (String op : login.authorityCode()) {
                if (userContext.getAuthority()
                        .stream()
                        .map(m -> m.getAuthorityCode())
                        .anyMatch(f -> f.equalsIgnoreCase(op))) {
                    UserHolder.set(userContext);
                    return true;
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
                return true;
            }
        }

        throw new AppException(40001, String.format("this user %s,operation no authority,operation code is %s"
                , userContext.getTrueName()
                , Arrays.toString(login.authorityCode())));
    }
}
