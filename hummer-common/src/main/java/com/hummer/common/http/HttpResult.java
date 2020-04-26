package com.hummer.common.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpResponse;

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
}