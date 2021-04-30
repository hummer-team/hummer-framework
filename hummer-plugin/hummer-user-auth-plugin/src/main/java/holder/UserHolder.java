package holder;

import com.alibaba.fastjson.JSON;
import com.hummer.common.exceptions.AppException;
import context.UserContext;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Map;

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

    public static String getUserName() {
        UserContext context = get();
        return context == null ? null : context.getUserName();
    }

    public static String getUserNameNotNull() {
        String userName = getUserName();
        if (StringUtils.isEmpty(userName)) {
            throw new AppException(40101, "this current user id is null,please login");
        }
        return userName;
    }

    public static <T> T getDateValueNotNull(String key, Class<T> cla) {
        Map<String, Object> map = getNotNull().getData();
        if (MapUtils.isEmpty(map)) {
            throw new AppException(50000, "this current user id is null,please login");
        }
        return JSON.parseObject(JSON.toJSONString(map.get(key)), cla);
    }


    public static void set(@NotNull UserContext userContext) {
        LOCAL.set(userContext);
    }

    public static void clean() {
        LOCAL.remove();
    }

}
