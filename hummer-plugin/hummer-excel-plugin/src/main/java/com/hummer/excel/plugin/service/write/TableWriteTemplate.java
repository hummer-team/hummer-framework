package com.hummer.excel.plugin.service.write;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.hummer.common.utils.AppBusinessAssert;
import com.hummer.excel.plugin.handle.write.ExcelWriter;
import com.hummer.excel.plugin.model.write.TableWriteDataBo;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.List;

/**
 * TableWriteTemplate
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/4/7 15:02
 */
public class TableWriteTemplate implements ExcelWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableWriteTemplate.class);

    public static final TableWriteTemplate getInstance() {
        return new TableWriteTemplate();
    }

    @Override
    public void tableWrite(List<TableWriteDataBo> list, String sheetName, OutputStream os) {

        AppBusinessAssert.isTrue(CollectionUtils.isNotEmpty(list), 400, "export excel by table type " +
                ",list is empty");

        // 这里直接写多个table的案例了，如果只有一个 也可以直一行代码搞定，参照其他案例
        // 这里 需要指定写用哪个class去写
        com.alibaba.excel.ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(os).build();
            // 把sheet设置为不需要头 不然会输出sheet的头 这样看起来第一个table 就有2个头了
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).needHead(Boolean.FALSE).build();
            // 这里必须指定需要头，table 会继承sheet的配置，sheet配置了不需要，table 默认也是不需要
            for (int i = 0; i < list.size(); i++) {
                TableWriteDataBo item = list.get(i);
                WriteTable writeTable = EasyExcel.writerTable(i).needHead(item.isWithHead())
                        .head(item.getHeadClass()).build();
                // 写入会创建头
                excelWriter.write(item.getDataInfos(), writeSheet, writeTable);
            }
        } finally {
            // 千万别忘记finish 会帮忙关闭流
            if (excelWriter != null && os != null) {
                try {
                    excelWriter.finish();
                } catch (Exception e) {
                    LOGGER.debug("excelWriter finish fail,{}", e.getMessage());
                }

            }
        }
    }
}
