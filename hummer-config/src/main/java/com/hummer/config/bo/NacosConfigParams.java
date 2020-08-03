package com.hummer.config.bo;

import lombok.Data;

import java.util.List;
import java.util.Properties;

/**
 * NacosConfigParams
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/7/28 16:02
 */
@Data
public class NacosConfigParams {

    private Properties properties;

    private List<String> groupIdList;

    private List<String> dataIdList;
}
