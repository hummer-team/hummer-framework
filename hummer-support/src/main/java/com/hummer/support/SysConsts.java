package com.hummer.support;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/20 14:39
 **/
public class SysConsts {
    private SysConsts() {

    }

    public static final Integer SYS_ERROR_CODE = 50000;
    public static final String REQUEST_ID = "requestId";
    public static final String HEADER_REQ_TIME = "X-HJ-Request-Time";

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String DEFAULT_CHARSET_NAME = "UTF-8";

    public static class RestConsts {
        private RestConsts() {

        }

        public static final String INCLUDE_URL_PATTEER = "/*";
        public static final String REST_REQUESTILTER_EXCLUSIONS_URL = "rest.requestfilter.exclusions.url";
        public static final String EXCLUSIONS_URL_PATTEER = "/do_not_delete/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*";
        public static final String SERVER_IP = "serverIp";
        public static final String CLIENT_IP = "clientIp";
        public static final String MVC_SERIALIZERFEATURE = "mvc.serializerFeature";
        public static final String SYSTEM_REMOTE_IP_SPLIT_CHAR = "system.remote.ip.split.char";
        public static final String REST_REQUESTILTER_IINCLUDE_URL = "rest.requestfilter.include.url";
    }


    public static class DaoConsts {
        private DaoConsts() {

        }

        public static final String SHOW_SQL = "show.sql";
        public static final String SHOW_TIMEOUT = "sql.timeout";
        public static final String SQL_SESSION_TEMPLATE_NAME = "jdbcTemplate";
        public static final String MYBATIS_BASE_PACKAGE ="mybatis.base.package";
        public static final String DB_NAME_KEY ="db.names";
        public static final String DB_PREFIX ="jdbc.";
        public static final String JDBC_TESTWHILEIDLE = "jdbc.testWhileIdle";
        public static final String JDBC_TESTONBORROW = "jdbc.testOnBorrow";
        public static final String JDBC_TESTONRETURN = "jdbc.testOnReturn";
        public static final String JDBC_VALIDATIONQUERYTIMEOUT = "jdbc.validationQueryTimeout";
        public static final String JDBC_REMOVEABANDONED = "jdbc.removeAbandoned";
        public static final String JDBC_REMOVEABANDONEDTIMEOUT = "jdbc.removeAbandonedTimeout";
        public static final String JDBC_LOGABANDONED = "jdbc.logAbandoned";
        public static final String DRUID_SHOW_TIMEOUT = "druid.sql.timeout";
        public static final String DRUID_SHOW_SQL = "druid.show.sql";
        public static final String DRUID_MERGE_SQL = "druid.merge.sql";
        public static final String JDBC_CHECK_SQL="jdbc.check.sql";
        public static final String DRUID_SELECT_UNION_CHECK = "druid.select.union.check";
    }
}
