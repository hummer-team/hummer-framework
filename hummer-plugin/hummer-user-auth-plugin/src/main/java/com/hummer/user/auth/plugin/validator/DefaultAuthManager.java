package com.hummer.user.auth.plugin.validator;

import com.hummer.user.auth.plugin.annotation.UserAuthorityAnnotation;
import com.hummer.common.utils.AppBusinessAssert;
import com.hummer.user.auth.plugin.context.UserContext;
import com.hummer.user.auth.plugin.holder.UserHolder;

/**
 * DefaultAuthManager
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/3/23 18:12
 */
public class DefaultAuthManager implements AuthManager {

    private AuthValidator authValidator;

    public DefaultAuthManager(AuthValidator authValidator) {
        this.authValidator = authValidator;
        init();
    }

    private void init() {
        if (this.authValidator == null) {
            authValidator = new DefaultAuthValidator();
        }
    }

    @Override
    public void doAuth(UserAuthorityAnnotation annotation) {

        UserContext userContext = authValidator.valid(annotation);
        AppBusinessAssert.isTrue(userContext != null
                , 41002, "operation no permission");
        handleContext(userContext);
    }

    @Override
    public void handleContext(UserContext userContext) {
        UserHolder.set(userContext);
    }


}
