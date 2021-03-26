package com.hummer.common.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * @author bingy
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HttpResult {
    private int status;
    private String result;
    private HttpResponse httpResponse;


    public HttpResult(int status, String result) {
        this.status = status;
        this.result = result;
    }

    public HttpResult(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public String toString() {

        return "HttpResult[" +
                "status=" + status +
                ", result=" + result +
                ", respEntity=" + tryRespMessage() +
                "]";
    }

    private String tryRespMessage() {
        try {
            return httpResponse == null ? null : EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        } catch (Throwable e) {
            return null;
        }
    }
}