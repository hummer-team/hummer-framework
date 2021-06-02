package com.hummer.nacos.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/9/1 13:02
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class CustomItemBo {
    private Integer b;

    private Integer c;
}
