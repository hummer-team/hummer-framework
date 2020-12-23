package com.hummer.api.web;

import com.google.common.collect.Lists;
import com.hummer.api.dto.QueryStringDto;
import com.hummer.common.utils.ObjectCopyUtils;
import com.hummer.rest.annotations.BindRestParameterSimpleModel;
import com.hummer.rest.model.ResourceResponse;
import com.hummer.rest.model.request.ResourcePageReqDto;
import com.hummer.rest.model.response.ResourcePageRespDto;
import com.hummer.rest.utils.ParameterAssertUtil;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/v1")
public class MessageCoderController {
    @GetMapping(value = "/message-coder")
    public ResourceResponse<QueryStringDto> getMessageCoder(@BindRestParameterSimpleModel @Valid
                                                                 QueryStringDto queryStringDto
        , Errors errors) {
        ParameterAssertUtil.assertRequestFirstValidated(errors);
        queryStringDto.setUuId("456");
        return ResourceResponse.ok(ObjectCopyUtils.copy(queryStringDto, QueryStringDto.class));
    }


    @PostMapping(value = "/message-coder2")
    public ResourceResponse<QueryStringDto> postMessageCoder(@RequestBody @Valid
                                                             QueryStringDto queryStringDto
        , Errors errors) {
        ParameterAssertUtil.assertRequestFirstValidated(errors);
        queryStringDto.setUuId("456dddffff");
        return ResourceResponse.ok(ObjectCopyUtils.copy(queryStringDto, QueryStringDto.class));
    }

    @PostMapping(value = "/message-coder3")
    public ResourceResponse<ResourcePageRespDto<QueryStringDto>> postMessageCoder3(
        @RequestBody @Valid ResourcePageReqDto<QueryStringDto> queryStringDto
        , Errors errors) {
        ParameterAssertUtil.assertRequestFirstValidated(errors);
        queryStringDto.getQueryObject().setUuId("456dddffff");
        return ResourceResponse.ok(ResourcePageRespDto.builderPage(1,10,100,
            Collections.singletonList(queryStringDto.getQueryObject())));
    }
}
