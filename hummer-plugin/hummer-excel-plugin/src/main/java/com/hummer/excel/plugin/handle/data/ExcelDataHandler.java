package com.hummer.excel.plugin.handle.data;

import java.util.List;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/6/28 15:27
 */
public interface ExcelDataHandler<T> {

    default void handle(T data) {
    }

    void handle(List<T> list);
}
