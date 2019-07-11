package com.hummer.common.security;

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
        KeyPair keyPair = Rsa.newKeyPair();
        System.out.println(new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded())));
        System.out.println(new String(Base64.encodeBase64(keyPair.getPublic().getEncoded())));


        System.out.println(new String(java.util.Base64.getEncoder().encode(keyPair.getPrivate().getEncoded())));
        System.out.println(new String(java.util.Base64.getEncoder().encode(keyPair.getPublic().getEncoded())));


        System.out.println(Rsa.newKeyPair2().toString());
    }


    @Test
    public void encrypted() {
        KeyPair keyPair = Rsa.newKeyPair();
        String encrypted1 = Rsa.encrypted("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq"
                , keyPair.getPublic());
        System.out.println(encrypted1);
        String decrypt2 = Rsa.decrypt(encrypted1, keyPair.getPrivate());
        System.out.println(decrypt2);
        Assert.assertEquals("{\"_uuid_\":\"3d5b0b25cece4343866d8a9850508dca\",\"_expired_\":1}", decrypt2);

        System.out.println("---------------");
        String encrypted = Rsa.encryptedByDefaultPublicKey("123344");
        System.out.println(encrypted);

        String originVal = Rsa.decryptByDefaultPrivateKey(encrypted);

        Assert.assertEquals("123344", originVal);
    }

    @Test
    public void sign() {
        String signValues = Rsa.sign("56677", Rsa.KeyPairTuple.DEFAULT_PRIVATE_KEY);
        System.out.println(signValues);
        System.out.println("------------------");
        boolean verify = Rsa.verify("56677", signValues, Rsa.KeyPairTuple.DEFAULT_PUBLIC_KEY);
        Assert.assertTrue(verify);
    }
}
