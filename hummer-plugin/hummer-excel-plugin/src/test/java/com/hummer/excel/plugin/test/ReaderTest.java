package com.hummer.excel.plugin.test;

import com.hummer.excel.plugin.service.ExcelTemplate;
import com.hummer.excel.plugin.test.model.HeaderDemo;

import java.io.File;

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
}
