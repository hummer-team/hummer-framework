package com.hummer.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.hummer.common.exceptions.AppException;
import com.hummer.common.serializeex.CustomQueryStringParse;
import com.hummer.common.serializeex.CustomQueryStringParseContainer;
import com.hummer.spring.plugin.context.PropertiesContainer;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2018/12/6 11:14
 **/
public class ParameterMapUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParameterMapUtil.class);

    /**
     * map key value convert to pojo model.
     *
     * @param map   map
     * @param clazz target class
     * @return pojo
     * @throws Exception
     */
    public static <T> T mapToPojo(Map<String, String[]> map, Class<T> clazz) {
        try {
            Map<String, Object> objectMap = convertQueryParamsToMap(map);
            T obj = clazz.newInstance();
            Field[] propertyDescriptors = clazz.getDeclaredFields();

            for (Field property : propertyDescriptors) {
                String key = property.getName();
                if (objectMap.containsKey(key.toLowerCase())) {
                    Object value = objectMap.get(key.toLowerCase());
                    if (value == null) {
                        continue;
                    }
                    value = parseValue(value, property.getType());
                    property.setAccessible(true);
                    property.set(obj, value);
                }
            }
            return obj;
        } catch (Exception e) {
            LOGGER.error("query String parameter convert to pojo failed,target class:`{}`,e "
                    , clazz.getSimpleName()
                    , e);
            throw new AppException(50000, e.getMessage());
        }
    }

    /**
     * convert query params to map
     *
     * @param map
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> convertQueryParamsToMap(Map<String, String[]> map) {
        if (MapUtils.isEmpty(map)) {
            return Collections.emptyMap();
        }
        Map<String, Object> params = new HashMap<>(map.size());
        int len;
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            len = entry.getValue().length;
            if (len == 1) {
                params.put(entry.getKey().toLowerCase(), entry.getValue()[0]);
            } else if (len > 1) {
                params.put(entry.getKey().toLowerCase(), entry.getValue());
            }
        }
        return params;
    }

    /**
     * parse target pojo property value-
     * <pre>
     *
     *     1,parse base type
     *     2,if parse base type failed then call custom parse
     *     3,call json  parse
     * </pre>
     *
     * @param value          Object
     * @param fieldTypeClass
     * @return pojo
     */
    public static Object parseValue(Object value, Class<?> fieldTypeClass) {
        if (value == null
                || Strings.isNullOrEmpty(value.toString())) {
            return null;
        }

        if (Long.class.isAssignableFrom(fieldTypeClass)
                || long.class.isAssignableFrom(fieldTypeClass)) {
            return Long.parseLong(value.toString());
        } else if (Integer.class.isAssignableFrom(fieldTypeClass)
                || int.class.isAssignableFrom(fieldTypeClass)) {
            return Integer.parseInt(value.toString());
        } else if (Float.class.isAssignableFrom(fieldTypeClass)
                || float.class.isAssignableFrom(fieldTypeClass)) {
            return Float.parseFloat(value.toString());
        } else if (Double.class.isAssignableFrom(fieldTypeClass)
                || double.class.isAssignableFrom(fieldTypeClass)) {
            return Double.parseDouble(value.toString());
        } else if (Date.class.isAssignableFrom(fieldTypeClass)) {
            return StringToDateConverterUtil.tryParseDateWithMultiFormats(value.toString());
        } else if (String.class.isAssignableFrom(fieldTypeClass)) {
            return value.toString();
        } else if (Boolean.class.isAssignableFrom(fieldTypeClass)
                || boolean.class.isAssignableFrom(fieldTypeClass)) {
            return Boolean.parseBoolean(value.toString());
        } else if (Collection.class.isAssignableFrom(fieldTypeClass)) {
            List<?> array;
            if (value instanceof String[]) {
                array = Lists.newArrayList((String[]) value);
            } else {
                array = Lists.newArrayList(value);
            }
            Collection<Object> returnVal = Lists.newArrayListWithCapacity(array.size());
            returnVal.addAll(array);
            return returnVal;
        } else if (OffsetDateTime.class.isAssignableFrom(fieldTypeClass)) {
            return OffsetDateTime.parse(value.toString());
        } else {
            //call custom parse , if exists
            CustomQueryStringParse queryStringParse = CustomQueryStringParseContainer.getParse(fieldTypeClass);
            if (queryStringParse != null) {
                return queryStringParse.parseValue(value, null, fieldTypeClass);
            }
            //use json parse
            return JSONObject.toJavaObject((JSON) value, fieldTypeClass);
        }
    }
}
