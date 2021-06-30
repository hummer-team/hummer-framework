package com.hummer.test;

import com.google.common.base.Splitter;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;

public class CommonTest {
    @Test
    public void url() {
        Assert.assertEquals("2021-06-24+11%3A46%3A57",URLEncoder.encode("2021-06-24 11:46:57"));
        URI uri = URI.create("http://local.gjpqqd.com:5929/Service/ERPService.asmx/EMallApi?method=emall.token.get&timestamp=2021-06-24+11%3A46%3A57&format=json&app_key=000210611170422897&v=1.0&sign=&sign_method=md5");
        Map<String, String> map = Splitter.on("&").withKeyValueSeparator("=").split(uri.getQuery());
        Assert.assertEquals("emall.token.get"
                , map.get("method"));
    }
}
