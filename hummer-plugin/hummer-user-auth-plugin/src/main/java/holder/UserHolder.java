package holder;

import com.hummer.common.exceptions.AppException;
import context.UserContext;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

public class UserHolder {

    private static final ThreadLocal<UserContext> LOCAL = new ThreadLocal<>();

    private UserHolder() {

    }

    public static UserContext get() {

        return LOCAL.get();
    }

    public static UserContext getNotNull() {

        UserContext context = get();
        if (context == null) {
            throw new AppException(40101, "this current user is null,please login");
        }
        return context;
    }

    public static String getUserId() {
        UserContext context = get();
        return context == null ? null : context.getUserId();
    }


    public static String getUserIdNotNull() {
        String userId = getUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new AppException(40101, "this current user id is null,please login");
        }
        return userId;
    }

    public static void set(@NotNull UserContext userContext) {
        LOCAL.set(userContext);
    }

    public static void clean() {
        LOCAL.remove();
    }

}
