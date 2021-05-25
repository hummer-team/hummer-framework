package com.hummer.user.auth.plugin.interceptor;

import com.hummer.core.SpringApplicationContext;
import com.hummer.user.auth.plugin.annotation.UserAuthorityAnnotation;
import com.hummer.user.auth.plugin.validator.AuthManager;
import com.hummer.user.auth.plugin.validator.DefaultAuthManager;
import lombok.extern.slf4j.Slf4j;
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
        //  用户权限校验
        verifyUserAuthority(handlerMethod);
        return true;
    }

    private void verifyUserAuthority(HandlerMethod handler) {
        UserAuthorityAnnotation annotation = getAnnotation(handler, UserAuthorityAnnotation.class);
        if (annotation == null) {
            return;
        }
        AuthManager managerBean = getAuthManagerBean(annotation.authManager());
        managerBean.doAuth(annotation);
    }

    private <T extends Annotation> T getAnnotation(HandlerMethod handler, Class<T> tClass) {

        T t = handler.getMethod().getAnnotation(tClass);
        if (t == null) {
            t = handler.getBeanType().getAnnotation(tClass);
        }
        return t;
    }

    private AuthManager getAuthManagerBean(Class<? extends AuthManager> managerClass) {
        try {
            return SpringApplicationContext.getBean(managerClass);
        } catch (Exception e) {
            log.debug("not fund target class=={}, AuthManager bean ", managerClass, e);
        }
        return SpringApplicationContext.getBean("defaultAuthManager", DefaultAuthManager.class);
    }
}