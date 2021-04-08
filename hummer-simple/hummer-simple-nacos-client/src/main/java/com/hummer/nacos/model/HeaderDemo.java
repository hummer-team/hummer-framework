package com.hummer.nacos.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/6/28 14:18
 */
@Data
public class HeaderDemo {

    @ExcelProperty("")
    private String name;

    public HeaderDemo() {
        init();
    }
    private void init(){
        this.name = "name";
    }
}
