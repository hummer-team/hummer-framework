package com.hummer.nacos.rest;

import com.hummer.nacos.service.CoordinatorImpl;
import com.hummer.rest.model.ResourceResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * SoaController
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/9/14 17:07
 */
@Api("SoaController")
@RestController
@RequestMapping("/soa")
public class SoaController {

    @Autowired
    private CoordinatorImpl coordinator;

    @ApiOperation("rollBack")
    @GetMapping("/roll-back")
    public ResourceResponse<String> rollBack(
            @RequestParam("flag") boolean flag
    ) {

        coordinator.testSoa(flag);

        return ResourceResponse.ok("ok");
    }
}
