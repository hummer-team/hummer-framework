package com.hummer.user.plugin.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * UserBasicInfoDepartmentReqDto
 *
 * @author chen wei
 * @version 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 * </p>
 * @date 2020/6/24 18:38
 */
@Data
public class UserBasicInfoPluginReqDto {

    @ApiModelProperty("部门类型1、采购部,2、质检部,3、仓管部,4、客服部,5、财务部,6、风控部,7、市场运营部")
    private List<Integer> departmentTypes;

    @ApiModelProperty("角色枚举10、采购部,20、质检部,30、仓管部,31、发货员,32、理货员,33、打包员,40、客服部,50、财务部,60、风控部,70、市场运营部")
    private List<Integer> roleTypes;
}
