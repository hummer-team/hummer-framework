package com.hummer.redis.plugin.test.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hummer.common.http.HttpSyncClient;
import com.hummer.common.utils.ObjectCopyUtils;
import com.hummer.core.PropertiesContainer;
import com.hummer.redis.plugin.test.model.DemoReqDto;
import com.hummer.redis.plugin.test.model.DemoRespDto;
import com.hummer.redis.plugin.test.model.ExceptionReqDto;
import com.hummer.rest.model.ResourceResponse;
import com.hummer.rest.model.request.ResourcePageReqDto;
import com.hummer.rest.model.response.ResourcePageRespDto;
import com.hummer.rest.utils.ParameterAssertUtil;
import com.hummer.rest.utils.ResponseUtil;
import com.hummer.user.plugin.constants.Constants;
import com.hummer.user.plugin.dto.request.UserBasicInfoPluginReqDto;
import com.hummer.user.plugin.dto.response.UserBasicInfoPluginRespDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/4/28 11:29
 */
@Api("测试接口集")
@RestController
@RequestMapping("/test")
@Validated
public class TestController {

    @ApiOperation("自定义异常测试")
    @PostMapping("/customException")
    public ResourceResponse<String> customException(
            @RequestBody @Valid ExceptionReqDto reqDto, Errors errors
    ) {
        ParameterAssertUtil.assertRequestFirstValidated(400, errors);

        return ResourceResponse.ok(reqDto.getPageNum().toString());
    }


    @ApiOperation("自定义page")
    @PostMapping("/page")
    public ResourceResponse<ResourcePageRespDto<DemoRespDto>> queryPage(
            @RequestBody @Valid ResourcePageReqDto<DemoReqDto> reqDto, Errors errors
    ) {
        ResourcePageRespDto<DemoRespDto> dto = ObjectCopyUtils.copy(reqDto, ResourcePageRespDto.class);

        dto.setTotalCount(10);
        return ResourceResponse.ok(dto);
    }

    @ApiOperation("http请求异常通知")
    @PostMapping("/http/warn/notify")
    public ResourceResponse<List<UserBasicInfoPluginRespDto>> httpSendingNotify(@RequestBody @Valid UserBasicInfoPluginReqDto reqDto) {
        if (reqDto == null) {
            reqDto = new UserBasicInfoPluginReqDto();
        }
        String url = String.format("%s/v1/user/query/department/basic/info/list"
                , PropertiesContainer.valueOfString("authority.service.host", Constants.ServiceRouteHost.AUTHORITY_SERVICE_HOST));
        String response = HttpSyncClient.sendHttpPostByRetry(url
                , JSON.toJSONString(reqDto)
                , PropertiesContainer.valueOf("authority.service.call.timeout.millis", Long.class, 5000L)
                , TimeUnit.MILLISECONDS
                , 1);

        return ResourceResponse.ok(ResponseUtil.parseResponseV2WithStatus(response
                , new TypeReference<ResourceResponse<List<UserBasicInfoPluginRespDto>>>() {
                }));

    }


}
