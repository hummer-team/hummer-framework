package com.hummer.excel.plugin.test;

import com.alibaba.excel.metadata.BaseRowModel;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TestFileUtil {


    public static InputStream getResourcesFileInputStream(String fileName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("" + fileName);
    }

    public static String getPath() {
        return TestFileUtil.class.getResource("/").getPath();
    }

    public static File createNewFile(String pathName) {
        File file = new File(getPath() + pathName);
        if (file.exists()) {
            file.delete();
        } else {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
        }
        return file;
    }

    public static File readFile(String pathName) {
        return new File(getPath() + pathName);
    }

    public static File readUserHomeFile(String pathName) {
        return new File(System.getProperty("user.home") + File.separator + pathName);
    }

    public static void sendExcelResponse(HttpServletResponse response, String fileName,
                                         List<? extends BaseRowModel> list, Class cla) {
        if (response == null) {
            return;
        }
        response.setContentType("multipart/form-data");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        try {
            ServletOutputStream out = response.getOutputStream();
            // ExcelUtil.writeExcelResponse(fileName, out, list, cla);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.flushBuffer();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
