package com.hummer.common.http;

import com.hummer.core.PropertiesContainer;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.time.Duration;

public class CustomConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {

    private static final long DEFAULT_KEEP_ALIVE = PropertiesContainer.valueOf(
            "httpClient.pool.keepAliveTime"
            , Long.class
            ,Duration.ofMinutes(18).toMillis());

    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        // Honor 'keep-alive' header
        HeaderElementIterator it = new BasicHeaderElementIterator(
                response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
            HeaderElement he = it.nextElement();
            String param = he.getName();
            String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout")) {
                return Long.parseLong(value) * 1000;
            }
        }

        return DEFAULT_KEEP_ALIVE;
    }
}

