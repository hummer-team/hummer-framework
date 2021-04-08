package com.hummer.nacos.rest;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.hummer.common.utils.HttpServletResponseUtil;
import com.hummer.excel.plugin.model.write.TableWriteDataBo;
import com.hummer.excel.plugin.service.write.TableWriteTemplate;
import com.hummer.nacos.model.HeaderDemo;
import com.hummer.nacos.model.HeaderDemo2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ExcelController
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/4/7 18:02
 */
@Api(tags = "excel options")
@RestController
@RequestMapping("/v1/test/excel")
public class ExcelController {


    @ApiOperation("多标题栏导出")
    @GetMapping("/export/table")
    public void queryUserContext(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<TableWriteDataBo> list = new ArrayList<>(2);
        TableWriteDataBo bo = new TableWriteDataBo();
        bo.setHeadClass(HeaderDemo.class);
        bo.setDataInfos(data(1, HeaderDemo.class));
        TableWriteDataBo bo2 = new TableWriteDataBo();
        bo2.setHeadClass(HeaderDemo2.class);
        bo2.setDataInfos(data(5, HeaderDemo2.class));
        bo2.setWithHead(true);
        list.add(bo);
        list.add(bo2);
        String sheet = "table";
        String fileName = "tableWrite_" + System.currentTimeMillis() + ExcelTypeEnum.XLSX.getValue();
        ServletOutputStream os = response.getOutputStream();
        HttpServletResponseUtil.composeExcelResponseHeaders(response, fileName);
        TableWriteTemplate.getInstance().tableWrite(list, sheet, os);
    }

    private <T> List<T> data(int size, Class<T> cla) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            try {
                list.add(cla.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
