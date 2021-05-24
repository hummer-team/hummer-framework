package com.hummer.nacos.rest;

import com.hummer.user.auth.plugin.annotation.UserAuthorityAnnotation;
import com.alibaba.fastjson.JSONObject;
import com.hummer.common.utils.CommonUtil;
import com.hummer.rest.model.ResourceResponse;
import com.hummer.user.auth.plugin.context.UserContext;
import com.hummer.user.auth.plugin.holder.UserHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * UserAuthController
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/3/25 15:18
 */
@Api(tags = "UserAuthController")
@RestController
@RequestMapping("/v1/test/user/auth")
public class UserAuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthController.class);

    @ApiOperation("获取所有配置")
    @PostMapping("/valid")
    public ResourceResponse<UserContext> validUserAuth(@RequestBody @Valid Map<String, Object> params) {
        LOGGER.debug("params == {},", JSONObject.toJSONString(params));

        UserContext context = new UserContext();
        Map<String, Object> map = new HashMap<>();
        map.put("a", "1");
        map.put("b", "2");
        context.setUserId(CommonUtil.getUuid());
        context.setUserName(context.getUserId() + "_name");
        context.setData(map);
        return ResourceResponse.ok(context);

    }

    @ApiOperation("获取当前访问用户信息")
    @GetMapping("/context")
    @UserAuthorityAnnotation
    public ResourceResponse<UserContext> queryUserContext(
    ) {

        return ResourceResponse.ok(UserHolder.get());
    }

}
