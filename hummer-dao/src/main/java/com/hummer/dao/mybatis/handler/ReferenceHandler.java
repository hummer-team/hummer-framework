package com.hummer.dao.mybatis.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/7/12 15:38
 **/
public class ReferenceHandler<T> extends BaseTypeHandler<Collection<Map<String,Object>>> {

    private Class<T> aClass;

    public ReferenceHandler(Class<T> tClass) {
        this.aClass = tClass;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Collection<Map<String,Object>> parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public Collection<Map<String,Object>> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }

    @Override
    public Collection<Map<String,Object>> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }

    @Override
    public Collection<Map<String,Object>> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }

    private Collection<Map<String,Object>> parseJson(String json) {
        return JSON.parseObject(json, new TypeReference<Collection<Map<String, Object>>>() {
        });
    }
}
