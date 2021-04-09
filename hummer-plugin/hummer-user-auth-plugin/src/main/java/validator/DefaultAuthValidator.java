package validator;

import annotation.UserAuthorityAnnotation;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.hummer.common.http.HttpSyncClient;
import com.hummer.common.utils.AppBusinessAssert;
import com.hummer.core.PropertiesContainer;
import com.hummer.rest.model.ResourceResponse;
import com.hummer.rest.utils.ResponseUtil;
import context.UserContext;
import holder.RequestContextHolder;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

/**
 * DefaultAuthValidator
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/3/23 11:16
 */
public class DefaultAuthValidator implements AuthValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAuthValidator.class);

    @Override
    public UserContext valid(UserAuthorityAnnotation annotation) {
        ValidParams params = parsingAnnotation(annotation);
        validParams(params);
        return validAuth(params);
    }

    public void validParams(ValidParams params) {

        verifyTicketsNotNull(params.getTokenMap());
    }

    public UserContext validAuth(ValidParams params) {
        AppBusinessAssert.isTrue(!StringUtils.isEmpty(params.getApiUrl())
                , 40000, "user.auth.valid.api.url not exists");
        String response
                = HttpSyncClient.sendHttpPostByRetry(params.getApiUrl(), JSONObject.toJSONString(params), 1);

        try {
            return ResponseUtil.parseResponseV2WithStatus(response, new TypeReference<ResourceResponse<UserContext>>() {
            });
        } catch (Exception e) {
            LOGGER.debug("user auth valid fail,params == {}", JSONObject.toJSONString(params));
        }
        return null;
    }

    private void verifyTicketsNotNull(Map<String, String> headers) {
        String headerKeys = PropertiesContainer.valueOfString("ticket.request.notnull.keys");
        if (StringUtils.isEmpty(headerKeys)) {
            return;
        }
        String[] keys = headerKeys.split(",");
        for (String key : keys) {
            if (StringUtils.isEmpty(key)) {
                continue;
            }
            AppBusinessAssert.isTrue(headers != null && !StringUtils.isEmpty(RequestContextHolder.get(key))
                    , 41001, String.format("this request ticket %s not exists.", key));
        }
    }

    private Map<String, String> composeTokens(String[] tokenKeys) {

        return parsingTokenKeys(getTokensIfNull(tokenKeys));
    }

    private Map<String, String> parsingTokenKeys(String[] tokenKeys) {
        if (ArrayUtils.isEmpty(tokenKeys)) {
            return MapUtils.EMPTY_MAP;
        }
        Map<String, String> map = Maps.newHashMapWithExpectedSize(tokenKeys.length);
        for (String key : tokenKeys) {
            map.put(key, RequestContextHolder.get(key));
        }
        return map;
    }

    private String[] getTokensIfNull(String[] tokenKeys) {
        if (!ArrayUtils.isEmpty(tokenKeys)) {
            return tokenKeys;
        }
        String tokenKeyStr = PropertiesContainer.valueOfString("user.auth.token.keys");
        if (StringUtils.isEmpty(tokenKeyStr)) {
            return null;
        }
        return tokenKeyStr.split(",");
    }

    private ValidParams parsingAnnotation(UserAuthorityAnnotation annotation) {
        return ValidParams.builder().apiUrl(getValidApiUrl(annotation.validApi()))
                .authCodes(Arrays.asList(annotation.authorityCodes()))
                .authCondition(annotation.authorityCondition())
                .tokenMap(composeTokens(annotation.userTokens()))
                .build();

    }

    public String getValidApiUrl(String apiUrl) {
        if (StringUtils.isEmpty(apiUrl)) {
            apiUrl = PropertiesContainer.valueOfString("user.auth.valid.api.url");
        }
        return apiUrl;
    }
}
