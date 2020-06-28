package com.hummer.excel.plugin.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.hummer.common.exceptions.AppException;
import com.hummer.excel.plugin.handle.data.ExcelDataHandler;
import com.hummer.excel.plugin.handle.read.ExcelReader;
import com.hummer.excel.plugin.handle.write.ExcelWriter;
import com.hummer.excel.plugin.listener.DataEasyListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * ExcelTemplate
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/6/28 14:34
 */
public class ExcelTemplate<T> implements ExcelReader<T>, ExcelWriter<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelTemplate.class);

    private final Class<T> cla;

    private ExcelTemplate(Class<T> cla) {
        this.cla = cla;
    }

    public static <T> ExcelTemplate<T> getInstance(Class<T> cla) {
        return new ExcelTemplate<T>(cla);
    }

    @Override
    public List<T> readTopSheetSync(MultipartFile multipartFile) {
        long start = System.currentTimeMillis();
        assertExcelFile(multipartFile);
        try {
            List<T> list = EasyExcel.read(multipartFile.getInputStream())
                    .head(cla)
                    .ignoreEmptyRow(true)
                    .sheet(0)
                    .doReadSync();
            long end = System.currentTimeMillis();
            LOGGER.info("readTopSheetSync fileName=={}, cost time =={}", multipartFile.getOriginalFilename(), end - start);
            return list;
        } catch (IOException e) {
            LOGGER.error("excel reading multipartFile.getInputStream fail ", e);
            throw new AppException(50001, "文件读取错误");
        }
    }

    public void readTopSheet(MultipartFile multipartFile, ExcelDataHandler<T> excelDataHandler) {
        long start = System.currentTimeMillis();
        assertExcelFile(multipartFile);
        DataEasyListener<T> listener = DataEasyListener.<T>builder().excelDataHandler(excelDataHandler).build();
        try {
            EasyExcel.read(multipartFile.getInputStream())
                    .registerReadListener(listener)
                    .head(cla)
                    .ignoreEmptyRow(true)
                    .sheet(0).build();
            long end = System.currentTimeMillis();
            LOGGER.info("readTopSheet fileName=={},fileLen=={}, cost time =={}", multipartFile.getOriginalFilename(),
                    multipartFile.getSize(), end - start);
        } catch (IOException e) {
            LOGGER.error("excel reading multipartFile.getInputStream fail ", e);
            throw new AppException(50001, "文件读取错误");
        }
    }

    public void readTopSheet(File file, ExcelDataHandler<T> excelDataHandler) {
        long start = System.currentTimeMillis();
        assertExcelFile(file);
        DataEasyListener<T> listener = DataEasyListener.<T>builder().excelDataHandler(excelDataHandler).build();
        try {
            EasyExcel.read(new FileInputStream(file))
                    .registerReadListener(listener)
                    .head(cla)
                    .ignoreEmptyRow(true)
                    .excelType(ExcelTypeEnum.XLSX)
                    .doReadAll();
        } catch (FileNotFoundException e) {
            LOGGER.error("excel reading FileInputStream fail ", e);
            throw new AppException(50001, "文件读取错误");
        }
        long end = System.currentTimeMillis();
        LOGGER.info("readTopSheet fileName=={},fileLen=={} cost time =={}", file.getName(), file.length(), end - start);
    }

    @Override
    public void write(List<T> list) {

    }

    private static final List<String> SUFFIX_ARR = Arrays.asList(".xls", ".XLS", ".xlsx", ".XLSX");

    private void assertExcelFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new AppException(40001, "传入文件是空文件");
        }
        assertFileType(multipartFile.getOriginalFilename());
    }

    private void assertExcelFile(File file) {
        if (file == null || !file.isFile()) {
            throw new AppException(40001, "传入文件是空文件");
        }
        assertFileType(file.getName());
    }

    private void assertFileType(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new AppException(40001, "传入文件格式不符");
        }
        String suffix = name.substring(name.lastIndexOf("."));
        if (StringUtils.isEmpty(name) || !SUFFIX_ARR.contains(suffix)) {
            throw new AppException(40001, "传入文件格式不符");
        }
    }
}
