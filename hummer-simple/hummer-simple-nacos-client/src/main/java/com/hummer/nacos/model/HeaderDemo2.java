package com.hummer.nacos.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

/**
 * HeaderDemo2
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/4/7 15:48
 */
@Data
public class HeaderDemo2 {

    @ExcelProperty("公司")
    private String name;

    @ExcelProperty("营业年份")
    private Integer age;

    @ExcelProperty("创建日期")
    private Date birthDay;

    public HeaderDemo2() {
        init();
    }
    private void init(){
        this.name = "name2";
        this.age = 2;
        this.birthDay = new Date();
    }
}
