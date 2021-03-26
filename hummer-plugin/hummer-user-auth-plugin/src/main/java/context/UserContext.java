package context;

import lombok.Data;

/**
 * @author chenwei
 */
@Data
public class UserContext {

    private String userId;

    private String userName;

    private Object data;
}
