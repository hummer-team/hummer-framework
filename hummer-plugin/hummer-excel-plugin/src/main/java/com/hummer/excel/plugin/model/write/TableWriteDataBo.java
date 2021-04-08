package com.hummer.excel.plugin.model.write;

import lombok.Data;

import java.util.List;

/**
 * TableWriteDataBo
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/4/7 15:07
 */
@Data
public class TableWriteDataBo {

    private Class<?> headClass;

    private List<?> dataInfos;

    private boolean withHead;
}
