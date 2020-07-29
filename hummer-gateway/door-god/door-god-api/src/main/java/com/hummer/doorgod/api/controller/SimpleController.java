package com.hummer.doorgod.api.controller;


import com.hummer.common.resource.ResourceResponse;
import com.hummer.common.utils.ParameterAssertUtil;
import com.hummer.doorgod.facade.SimpleDemoFacade;
import com.hummer.doorgod.facade.dto.request.SimpleDemoSaveReqDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * Delivery Line enter
 */
@RestController
@RequestMapping(value = "/v1")
@Api(value = "this simple controller demo for learning")
@Validated
public class SimpleController {
    @Autowired
    private SimpleDemoFacade simpleDemoFacade;

    @PostMapping(value = "/simple/save")
    @ApiOperation(value = "this is save batch info to db demo")
    public ResourceResponse save(@RequestBody @Valid SimpleDemoSaveReqDto reqDto
            , Errors errors) {
        ParameterAssertUtil.assertRequestFirstValidated(errors);
        simpleDemoFacade.save(reqDto);
        return ResourceResponse.ok();
    }

    @GetMapping(value = "/simple/query-single")
    @ApiOperation(value = "this is query demo")
    public ResourceResponse save(@RequestParam("id")
                                 @NotEmpty(message = "id can't null")
                                 @Length(max = 100, message = "max length 100 char.")
                                 String id) {
        return ResourceResponse.ok(simpleDemoFacade.querySingleById(id));
    }
}
