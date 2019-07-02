package com.hummer.common.security;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * wrapper aes decrypt encrypt
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/7/1 15:51
 **/
public class Aes {
    private static final Logger LOGGER = LoggerFactory.getLogger(Aes.class);
    private static final String DEFAULT_KEY = "randomwwwwbing00";
    private static final String DEFAULT_IV = "Bar1234|Bar@2345";

    private Aes() {

    }

    /**
     * use default  key v encrypted value.
     *
     * @param value origin value
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/1 16:16
     * @since 1.0.0
     **/
    public static String encryptToBase64ByDefaultKeyIv(final String value) {
        Validate.notEmpty(value, "value can not null");
        return encryptToBase64(DEFAULT_KEY, DEFAULT_IV, value);
    }

    /**
     * use  default key iv decrypt.
     *
     * @param encryptValue encrypted value
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/1 16:27
     * @since 1.0.0
     **/
    public static String decryptFormBase64ByDefaultKeyIv(final String encryptValue) {
        Validate.notEmpty(encryptValue, "value can not null");
        return decrypt(DEFAULT_KEY, DEFAULT_IV, Base64.decodeBase64(encryptValue));
    }

    /**
     * encrypt value return base64 format
     *
     * @param key        key
     * @param initVector iv
     * @param value      value
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/1 16:42
     * @since 1.0.0
     **/
    public static String encryptToBase64(final String key, final String initVector, final String value) {
        byte[] bytes = encrypt(key, initVector, value);
        String base64 = Base64.encodeBase64String(bytes);
        LOGGER.info("encrypted string: {}", base64);
        return base64;
    }

    /**
     * encrypt
     *
     * @param value value
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/1 16:59
     * @since 1.0.0
     **/
    public static String encryptToStringByDefaultKeyIv(final String value) {
        byte[] bytes = encrypt(DEFAULT_KEY, DEFAULT_IV, value);
        return DatatypeConverter.printHexBinary(bytes);
    }


    /**
     * encrypt value return string format
     *
     * @param key        key
     * @param initVector iv
     * @param value      value
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/1 16:42
     * @since 1.0.0
     **/
    public static String encryptToString(final String key, final String initVector, final String value) {
        byte[] bytes = encrypt(key, initVector, value);
        return DatatypeConverter.printHexBinary(bytes);
    }

    /**
     * encrypt values,if exception then return null
     *
     * @param key        key
     * @param initVector init Vector
     * @param value      value
     * @return java.lang.String
     * @throws NullPointerException
     * @throws IllegalArgumentException
     * @author liguo
     * @date 2019/7/1 16:02
     * @since 1.0.0
     **/
    public static byte[] encrypt(final String key, final String initVector, final String value) {
        Validate.notEmpty(key, "key can not null");
        Validate.notEmpty(initVector, "init vector can not null");
        Validate.notEmpty(value, "value can not null");

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] bytes = cipher.doFinal(value.getBytes());
            LOGGER.info("encrypted is {}", bytes);
            return bytes;
        } catch (Exception ex) {
            LOGGER.error("aes encrypt exception key is {} init vector is {} value is {}"
                    , key
                    , initVector
                    , value
                    , ex);
        }

        return null;
    }

    /**
     * decrypt form base64 value
     *
     * @param key        key
     * @param initVector iv
     * @param encrypted  encrypted value
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/1 16:55
     * @since 1.0.0
     **/
    public static String decryptFormBase64(final String key, final String initVector, final String encrypted) {
        return decrypt(key, initVector, Base64.decodeBase64(encrypted));
    }

    /**
     * decrypt,use default key and iv
     *
     * @param encrypted encrypted string
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/1 17:28
     * @since 1.0.0
     **/
    public static String decryptByDefaultKeyIv(final String encrypted) {
        return decrypt(DEFAULT_KEY, DEFAULT_IV, DatatypeConverter.parseHexBinary(encrypted));
    }


    /**
     * decrypt
     *
     * @param key        key
     * @param initVector iv
     * @param encrypted  encrypted
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/1 16:55
     * @since 1.0.0
     **/
    public static String decrypt(final String key, final String initVector, final String encrypted) {
        return decrypt(key, initVector, DatatypeConverter.parseHexBinary(encrypted));
    }

    /**
     * decrypt values,if exception then return null
     *
     * @param key            key
     * @param initVector     init vector
     * @param encryptedBytes encrypted
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/1 16:02
     * @since 1.0.0
     **/
    private static String decrypt(final String key
            , final String initVector
            , byte[] encryptedBytes) {
        Validate.notEmpty(key, "key can not null");
        Validate.notEmpty(initVector, "init vector can not null");
        Validate.isTrue(encryptedBytes != null, "encrypted can not null");

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(encryptedBytes);

            return new String(original);
        } catch (Exception ex) {
            LOGGER.error("aes decrypt exception key is {} init vector is {} encrypted is {}"
                    , key
                    , initVector
                    , encryptedBytes
                    , ex);
        }

        return null;
    }
}
