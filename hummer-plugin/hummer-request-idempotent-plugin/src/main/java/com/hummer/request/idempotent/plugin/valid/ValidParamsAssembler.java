package com.hummer.request.idempotent.plugin.valid;

import java.util.Map;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/1/20 18:49
 */
public interface ValidParamsAssembler {

    /**
     * 校验参数
     * @param objs
     * @author chen wei
     * @date 2021/1/20
     * @return java.util.Map<java.lang.String,java.lang.String>
     */
    Map<String, String> assemble(Object... objs);
}
