package com.hummer.excel.plugin.handle.write;

import java.util.List;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/6/28 14:32
 */
public interface ExcelWriter<T> {

    public void write(List<T> list);
}
