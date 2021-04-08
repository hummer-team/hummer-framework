package com.hummer.excel.plugin.handle.write;

import com.hummer.excel.plugin.model.write.TableWriteDataBo;

import java.io.OutputStream;
import java.util.List;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/6/28 14:32
 */
public interface ExcelWriter {

    /**
     * 单个sheet内多个表格
     *
     * @param list
     * @param sheetName
     * @param os
     * @return void
     * @author chen wei
     * @date 2021/4/7
     */
    void tableWrite(List<TableWriteDataBo> list, String sheetName, OutputStream os);
}
