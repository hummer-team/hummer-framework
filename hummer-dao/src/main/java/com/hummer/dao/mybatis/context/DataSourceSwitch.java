package com.hummer.dao.mybatis.context;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/7/18 17:56
 **/
public class DataSourceSwitch {
    private DataSourceSwitch() {

    }

    public static void set(final String dataSourceName) {
        MultipleDataSourceMap.setDataSource(dataSourceName);
    }

    public static void clean() {
        MultipleDataSourceMap.clean();
    }

    public static boolean exists(final String dataSourceName){
        return MultipleDataSourceMap.exists(dataSourceName);
    }
}
