package com.hummer.common.test;

import com.hummer.common.security.Aes;
import com.hummer.common.security.Md5;
import com.hummer.common.security.Rsa;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/7/1 16:31
 **/
public class SecurityTest {
    @Test
    public void encryptToBase64ByDefault() {
        String origin = Aes.encryptToBase64ByDefaultKeyIv("12334");
        Assert.assertEquals("12334", Aes.decryptFormBase64ByDefaultKeyIv(origin));
    }


    @Test
    public void encrypt() {
        String origin = Aes.encryptToStringByDefaultKeyIv("1234555555555555555555555");
        System.out.println(origin);
        Assert.assertEquals("1234555555555555555555555", Aes.decryptByDefaultKeyIv(origin));
    }

    @Test
    public void md5() {
        String val = Md5.encryptMd5(123, "a", "B");
        System.out.println(val);

        String val2 = Md5.encryptMd5(String.format("%s%s%s", 123, "a", "B"));
        System.out.println(val2);

        Assert.assertEquals(val, val2);
    }


    @Test
    public void sha256() {
        String val = Md5.encryptSha256(123, "a", "B");
        System.out.println(val);

        String val2 = Md5.encryptSha256(String.format("%s%s%s", 123, "a", "B"));
        System.out.println(val2);

        Assert.assertEquals(val, val2);
    }

    @Test
    public void rsaNewKeyPair() {
        KeyPair keyPair = Rsa.createKeyPair();
        System.out.println(new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded())));
        System.out.println(new String(Base64.encodeBase64(keyPair.getPublic().getEncoded())));


        System.out.println(new String(java.util.Base64.getEncoder().encode(keyPair.getPrivate().getEncoded())));
        System.out.println(new String(java.util.Base64.getEncoder().encode(keyPair.getPublic().getEncoded())));


        System.out.println(Rsa.createKeyTuple().toString());
    }

    @Test
    public void rsa2(){

        String  val = "qqqqqqqqttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq";

        Rsa.KeyPairTuple keyPairTuple = Rsa.createKeyTuple();

        Rsa rsa = new Rsa(keyPairTuple.publicKey(),keyPairTuple.privateKey());
        String result = rsa.encryptByPublish(val);
        System.out.println(result);
        String result3 = rsa.decryptByPrivate(result);

        Assert.assertEquals(val,result3);
        //
        String result2 = rsa.encryptByPrivate(val);
        System.out.println(result2);
        Assert.assertEquals(val,rsa.decryptByPublic(result2));
    }



    @Test
    public void sign() {
        Rsa.KeyPairTuple keyPairTuple = Rsa.createKeyTuple();
        Rsa rsa = new Rsa(keyPairTuple.publicKey(),keyPairTuple.privateKey());
        String signValues = rsa.sign("56677");
        System.out.println(signValues);
        System.out.println("------------------");
        boolean verify = rsa.verify("56677", signValues);
        Assert.assertTrue(verify);
    }
}
