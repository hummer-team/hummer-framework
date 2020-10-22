package com.hummer.user.plugin.agent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.hummer.common.exceptions.AppException;
import com.hummer.common.http.HttpSyncClient;
import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import com.hummer.eureka.client.config.ServiceInstanceHolder;
import com.hummer.rest.model.ResourceResponse;
import com.hummer.rest.utils.ResponseUtil;
import com.hummer.user.plugin.dto.request.UserBasicInfoPluginReqDto;
import com.hummer.user.plugin.dto.response.UserBasicInfoPluginRespDto;
import com.hummer.user.plugin.user.TicketContext;
import com.hummer.user.plugin.user.UserContext;
import com.hummer.user.plugin.user.member.MemberTicketVerifyReqDto;
import com.hummer.user.plugin.user.member.MemberTicketVerifyRespDto;
import com.hummer.user.plugin.user.member.MemberUserContext;
import com.hummer.user.plugin.user.member.NetCoreResponse;
import com.hummer.user.plugin.user.member.NetCoreResponseParseUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AuthorityServiceAgent {
    private AuthorityServiceAgent() {

    }

    public static List<UserBasicInfoPluginRespDto> getUserBasicInfo(UserBasicInfoPluginReqDto reqDto) {
        if (reqDto == null) {
            reqDto = new UserBasicInfoPluginReqDto();
        }

        String applicationName = PropertiesContainer.valueOfStringWithAssertNotNull("authority.application.name");
        String host = SpringApplicationContext
                .getBean(ServiceInstanceHolder.class)
                .getServiceInstance(applicationName);

        String url = String.format("%s/v1/user/query/department/basic/info/list", host);
        String response = HttpSyncClient.sendHttpPostByRetry(url
                , JSON.toJSONString(reqDto)
                , PropertiesContainer.valueOf(String.format("%s.timeout.millis", applicationName)
                        , Long.class, 5000L)
                , TimeUnit.MILLISECONDS
                , 1);

        return ResponseUtil.parseResponseV2WithStatus(response
                , new TypeReference<ResourceResponse<List<UserBasicInfoPluginRespDto>>>() {
                });
    }


    public static UserContext getUserContext(final String ticket) {

        boolean urldecoding = PropertiesContainer.valueOf("ticket.need.urldecoding", Boolean.class, Boolean.FALSE);
        TicketContext ticketContext = new TicketContext();
        ticketContext.setTicket(getBase64Encode(ticket, urldecoding));
        if (StringUtils.isEmpty(ticketContext.getTicket())) {
            throw new AppException(41001, "this ticket is invalid");
        }
        ticketContext.setBase64(true);

        return getUserContext(ticketContext);
    }

    public static UserContext getUserContext(TicketContext reqDto) {

        String applicationName = PropertiesContainer.valueOfStringWithAssertNotNull("login.application.name");
        String host = SpringApplicationContext
                .getBean(ServiceInstanceHolder.class)
                .getServiceInstance(applicationName);

        String url = String.format("%s/v1/admin/ticket/verify-new", host);

        String response = HttpSyncClient.sendHttpPostByRetry(url
                , JSON.toJSONString(reqDto)
                , PropertiesContainer.valueOf(String.format("%s.timeout.millis", applicationName)
                        , Long.class, 5000L)
                , TimeUnit.MILLISECONDS
                , 1);
        return ResponseUtil.parseResponseV2WithStatus(response
                , new TypeReference<ResourceResponse<UserContext>>() {
                });
    }


    private static String getBase64Encode(String ticket, boolean decoding) {
        return decoding
                ? urlDecoding(ticket)
                : ticket;
    }

    private static String urlDecoding(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }


    public static String getMemberUserContext(final String ticket) {
        String applicationName = "user-auth.host.name";
        String host = PropertiesContainer.valueOfStringWithAssertNotNull(applicationName);
        String url = String.format("%s/api/Auth4Service", host);

        String response = HttpSyncClient.sendHttpPostByRetry(url
                , JSON.toJSONString(MemberTicketVerifyReqDto.builder().cookieValue(ticket).build())
                , PropertiesContainer.valueOf(String.format("%s.timeout.millis", applicationName)
                        , Long.class, 5000L)
                , TimeUnit.MILLISECONDS
                , 1);
        MemberTicketVerifyRespDto respDto = ResponseUtil.parseResponseV3WithStatus(response
                , new TypeReference<MemberTicketVerifyRespDto>() {
                });
        if (respDto.getCode() != 200 || respDto.getData() == null || StringUtils.isEmpty(respDto.getData().getUserId())) {
            return null;
        }
        return respDto.getData().getUserId();
    }

    public static MemberUserContext queryUserNameByMemberId(final String userId) {
        String applicationName = "user.service.host";
        String host = PropertiesContainer.valueOfString(applicationName, "http://userService.api.panli.com");
        String url = String.format("%s/api/user4service/GetUserInfoById?userId=%s", host, userId);

        String response = HttpSyncClient.sendHttpsGetByRetry(url
                , PropertiesContainer.valueOf(String.format("%s.timeout.millis", applicationName)
                        , Long.class, 5000L)
                , TimeUnit.MILLISECONDS
                , 2);
        NetCoreResponse<MemberUserContext> respDto = NetCoreResponseParseUtil.parsingNetResponseByAssert(response
                , new TypeReference<NetCoreResponse<MemberUserContext>>() {
                });
        return respDto.getData();
    }


}
