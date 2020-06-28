package com.hummer.excel.plugin.handle.read;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * ExcelReader
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/6/28 14:30
 */
public interface ExcelReader<T> {

    public List<T> readTopSheetSync(MultipartFile multipartFile);

}
