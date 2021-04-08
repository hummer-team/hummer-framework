package com.hummer.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * HttpServletResponseUtil
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/4/7 17:56
 */
public class HttpServletResponseUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServletResponseUtil.class);

    /**
     * EXCEL 导出HttpServletResponse返回
     *
     * @return void
     * @author chen wei
     * @date 2019/7/15
     */
    public static void composeExcelResponseHeaders(HttpServletResponse response, String fileName) {
        if (response == null) {
            return;
        }
        try {
            fileName = new String(fileName.getBytes(), "iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("excel response fileName iso-8859-1 encode fail,fileName=={} ", fileName, e);
        }
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}
