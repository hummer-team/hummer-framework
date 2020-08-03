package com.hummer.doorgod.support.mybatis;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * convert mysql column type json to `T` business type
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/7/9 15:15
 **/
public class JsonSimpleHandle<T> extends BaseTypeHandler<T> {
    private Class<T> aClass;

    public JsonSimpleHandle(Class<T> tClass) {
        this.aClass = tClass;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return JSON.parseObject(rs.getString(columnName),aClass);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return JSON.parseObject(rs.getString(columnIndex),aClass);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return JSON.parseObject(cs.getString(columnIndex),aClass);
    }
}
