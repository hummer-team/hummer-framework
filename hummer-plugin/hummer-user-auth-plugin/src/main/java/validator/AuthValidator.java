package validator;

import annotation.UserAuthorityAnnotation;
import context.UserContext;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/3/23 11:08
 */
public interface AuthValidator {

    UserContext valid(UserAuthorityAnnotation annotation);
}
