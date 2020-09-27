package com.hummer.soa.plugin.rollback;

/**
 * Created by lijinpeng on 2018/4/25.
 */
public class BizRollbackDescription {
    private String targetUrl;
    private String httpMethod;

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public String toString() {
        return "BizRollbackDescription{" +
                "targetUrl='" + targetUrl + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                '}';
    }
}
