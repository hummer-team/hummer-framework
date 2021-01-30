package org.mybatis.generator.internal.types;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import java.sql.Types;

/**
 * description java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 * </p>
 * @date 2020/6/16 13:45
 */
public class MyJavaTypeResolverDefaultImpl extends JavaTypeResolverDefaultImpl {

    public MyJavaTypeResolverDefaultImpl() {
        super();
        // 把数据库的 TINYINT 映射成 Integer
        super.typeMap.put(Types.TINYINT,
                new JdbcTypeInformation("TINYINT", new FullyQualifiedJavaType(Integer.class.getTypeName())));
        super.typeMap.put(Types.SMALLINT,
                new JdbcTypeInformation("SMALLINT", new FullyQualifiedJavaType(Integer.class.getTypeName())));
    }
}