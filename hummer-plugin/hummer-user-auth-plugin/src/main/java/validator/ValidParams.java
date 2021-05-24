package validator;

import annotation.AuthorityConditionEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * ValidParams
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/3/23 11:13
 */
@Data
@Builder
public class ValidParams {

    private String apiUrl;

    private String group;

    private Map<String, String> tokenMap;

    private List<String> authCodes;

    private AuthorityConditionEnum authCondition;
}
