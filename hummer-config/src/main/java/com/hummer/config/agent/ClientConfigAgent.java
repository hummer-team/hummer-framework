package com.hummer.config.agent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hummer.common.http.HttpSyncClient;
import com.hummer.config.dto.ClientConfigUploadReqDto;
import com.hummer.core.PropertiesContainer;
import com.hummer.rest.model.ResourceResponse;
import com.hummer.rest.utils.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ClientConfigAgent
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/7/24 15:47
 */
public class ClientConfigAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConfigAgent.class);

    public static void uploadConfig(ClientConfigUploadReqDto reqDto) {
        List<ClientConfigUploadReqDto> list = new ArrayList<>();
        list.add(reqDto);
        String url = String.format("%s/v1/business-log/write/%s"
                , PropertiesContainer.valueOfStringWithAssertNotNull("biz-log.service.host"), reqDto.getBusinessCode());

        String response = HttpSyncClient.sendHttpPostByRetry(url
                , JSON.toJSONString(list)
                , PropertiesContainer.valueOf("biz-log.service.call.timeout.millis", Long.class, 5000L)
                , TimeUnit.MILLISECONDS
                , 1);
        LOGGER.info("biz-log.service.call back == {}", response);
        ResponseUtil.parseResponseV2WithStatus(response
                , new TypeReference<ResourceResponse<Boolean>>() {
                });
    }

}
