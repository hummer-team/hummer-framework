package com.hummer.user.plugin.agent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hummer.common.exceptions.AppException;
import com.hummer.common.http.HttpSyncClient;
import com.hummer.core.PropertiesContainer;
import com.hummer.rest.model.ResourceResponse;
import com.hummer.rest.utils.ResponseUtil;
import com.hummer.user.plugin.user.TicketContext;
import com.hummer.user.plugin.user.UserContext;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

public class AuthorityServiceAgent {
    private AuthorityServiceAgent() {

    }

    public static UserContext getUserContext(final String ticket) {

        boolean urldecoding = PropertiesContainer.valueOf("ticket.need.urldecoding", Boolean.class, Boolean.FALSE);
        TicketContext ticketContext = new TicketContext();
        ticketContext.setTicket(getBase64Encode(ticket, urldecoding));
        if (StringUtils.isEmpty(ticketContext.getTicket())) {
            throw new AppException(45000, "this ticket is invalid");
        }
        ticketContext.setBase64(true);
        String url = String.format("%s/v1/admin/ticket/verify-new"
                , PropertiesContainer.valueOfString("login.service.host"));

        String response = HttpSyncClient.sendHttpPostByRetry(url
                , JSON.toJSONString(ticketContext)
                , PropertiesContainer.valueOf("login.service.call.timeout.millis", Long.class, 5000L)
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
}
