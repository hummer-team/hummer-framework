package com.hummer.common.http;

public class HttpAsyncClientWrapper {
    private HttpAsyncClientWrapper(){

    }

    public static HttpAsyncClient getSingleInstance() {
        return Inner.CLIENT;
    }

    private static class Inner {
        private static final HttpAsyncClient CLIENT = HttpAsyncClient.create();
    }
}
