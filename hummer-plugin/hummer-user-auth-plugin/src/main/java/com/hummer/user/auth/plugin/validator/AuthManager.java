package com.hummer.user.auth.plugin.validator;

import com.hummer.user.auth.plugin.annotation.UserAuthorityAnnotation;
import com.hummer.user.auth.plugin.context.UserContext;

/**
 * AuthManager
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/3/23 10:45
 */
public interface AuthManager {

    void doAuth(UserAuthorityAnnotation annotation);

    void handleContext(UserContext userContext);
}
