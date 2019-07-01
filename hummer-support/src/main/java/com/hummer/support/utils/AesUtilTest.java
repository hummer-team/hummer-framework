package com.hummer.support.utils;

import org.junit.Assert;
import org.junit.Test;


/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/7/1 16:31
 **/
public class AesUtilTest {
    @Test
    public void encryptToBase64ByDefault() {
        String origin = AesUtil.encryptToBase64ByDefaultKeyIv("12334");
        Assert.assertEquals("12334", AesUtil.decryptFormBase64ByDefaultKeyIv(origin));
    }


    @Test
    public void encrypt() {
        String origin = AesUtil.encryptToStringByDefaultKeyIv("1234555555555555555555555");
        System.out.println(origin);
        Assert.assertEquals("1234555555555555555555555", AesUtil.decryptByDefaultKeyIv(origin));
    }
}
