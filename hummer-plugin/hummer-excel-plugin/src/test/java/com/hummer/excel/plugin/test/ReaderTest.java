package com.hummer.excel.plugin.test;

import com.hummer.excel.plugin.model.write.TableWriteDataBo;
import com.hummer.excel.plugin.service.ExcelTemplate;
import com.hummer.excel.plugin.service.write.TableWriteTemplate;
import com.hummer.excel.plugin.test.model.HeaderDemo;
import com.hummer.excel.plugin.test.model.HeaderDemo2;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * ReaderTest
 *
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/6/28 14:16
 */
public class ReaderTest {

    public static void main(String[] args) {

        String path = "C:\\Users\\Administrator\\Desktop\\test.xlsx";
        File file = new File(path);
        if (file == null || !file.isFile()) {
            return;
        }
        ExcelTemplate<HeaderDemo> template = ExcelTemplate.getInstance(HeaderDemo.class);
        template.readTopSheet(file, null);
    }

    @Test
    public void testTableExcel() {
        List<TableWriteDataBo> list = new ArrayList<>(2);
        TableWriteDataBo bo = new TableWriteDataBo();
        bo.setHeadClass(HeaderDemo.class);
        bo.setDataInfos(data(1, HeaderDemo.class));
        TableWriteDataBo bo2 = new TableWriteDataBo();
        bo2.setHeadClass(HeaderDemo2.class);
        bo2.setDataInfos(data(5, HeaderDemo2.class));
        list.add(bo);
        list.add(bo2);
        String sheet = "模板";
        String fileName = TestFileUtil.getPath() + "tableWrite" + System.currentTimeMillis() + ".xlsx";
        File file = new File(fileName);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        TableWriteTemplate.getInstance().tableWrite(list, sheet, outputStream);
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
