package com.hummer.common.security;

import com.hummer.common.SysConsts;
import com.hummer.common.exceptions.SysException;
import org.apache.commons.codec.binary.Base64;
import sun.security.rsa.RSAPrivateKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;
import sun.security.util.DerValue;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * wrapper RSA decrypt encrypt and sign verify
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/7/1 17:53
 * @see <a href='https://www.zhihu.com/question/25912483'>www.zhihu.com/question/25912483</a>
 **/
public class Rsa {
    private static final int MAX_ENCRYPT_BLOCK = 117;
    private static final int MAX_DECRYPT_BLOCK = 128;

    private Rsa() {

    }

    /**
     * use default public key segment encrypted , return base64 string values
     *
     * @param data origin data
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/2 14:57
     * @since 1.0.0
     **/
    public static String encryptedByDefaultPublicKey(final String data) {
        try {
            PublicKey publicKey = RSAPublicKeyImpl.parse(
                    new DerValue(Base64.decodeBase64(KeyPairTuple.DEFAULT_PUBLIC_KEY)));
            return encrypted(data, publicKey);
        } catch (Throwable throwable) {
            throw new SysException(SysConsts.SYS_ERROR_CODE
                    , "encrypted by default public key failed", throwable);
        }
    }

    /**
     * use default private key segment decrypt,return origin string  values
     *
     * @param base64Data encrypted base64 string values
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/2 14:53
     * @since 1.0.0
     **/
    public static String decryptByDefaultPrivateKey(final String base64Data) {
        try {
            PrivateKey privateKey = RSAPrivateKeyImpl.parseKey(new DerValue(
                    Base64.decodeBase64(KeyPairTuple.DEFAULT_PRIVATE_KEY)));
            return decrypt(base64Data, privateKey);
        } catch (Throwable throwable) {
            throw new SysException(SysConsts.SYS_ERROR_CODE
                    , "decrypt by default private key failed", throwable);
        }
    }

    /**
     * encrypted data. public key use {@link Rsa#newKeyPair} result
     *
     * @param data      need encrypted string values
     * @param publicKey public key must is base64 format string values
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/2 15:56
     * @since 1.0.0
     **/
    public static String encrypted(final String data, final String publicKey) {
        try {
            PublicKey key = RSAPublicKeyImpl.parse(
                    new DerValue(Base64.decodeBase64(publicKey)));
            return encrypted(data, key);
        } catch (Throwable throwable) {
            throw new SysException(SysConsts.SYS_ERROR_CODE
                    , String.format("encrypted failed,input parameter %s", data), throwable);
        }
    }

    /**
     * decrypt data. private key use {@link Rsa#newKeyPair} result
     *
     * @param encryptedData encrypted string values
     * @param privateKey    private key
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/2 16:03
     * @since 1.0.0
     **/
    public static String decrypt(final String encryptedData, final String privateKey) {
        try {
            PrivateKey key = RSAPrivateKeyImpl.parseKey(new DerValue(
                    Base64.decodeBase64(privateKey)));
            return decrypt(encryptedData, key);
        } catch (Throwable throwable) {
            throw new SysException(SysConsts.SYS_ERROR_CODE
                    , "decrypt by default private key failed", throwable);
        }
    }

    /**
     * segment encrypted value return base64 encoding string
     *
     * @param data      origin data
     * @param publicKey public key
     * @return java.lang.String
     * @throws SysException
     * @author liguo
     * @date 2019/7/2 14:34
     * @since 1.0.0
     **/
    public static String encrypted(final String data, final PublicKey publicKey) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] dataBytes = data.getBytes("utf-8");
            byte[] encryptedBytes = segment(outputStream, cipher, dataBytes, MAX_ENCRYPT_BLOCK,true);
            return Base64.encodeBase64String(encryptedBytes);
        } catch (Throwable throwable) {
            throw new SysException(SysConsts.SYS_ERROR_CODE, "rsa encrypted failed", throwable);
        }
    }

    /**
     * segment decrypt data return origin string
     *
     * @param base64Data encrypted values is base64 encoding
     * @param privateKey private key
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/2 14:41
     * @since 1.0.0
     **/
    public static String decrypt(final String base64Data, final PrivateKey privateKey) {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");


//        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
//        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);

            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey.getEncoded()));
            RSAPrivateKey privateKey2 = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptData = Base64.decodeBase64(base64Data);
            byte[] decryptedData = segment(outputStream, cipher, decryptData, 117,false);
            return new String(decryptedData, "utf-8");
        } catch (Throwable throwable) {
            throw new SysException(SysConsts.SYS_ERROR_CODE, "rsa decrypt failed", throwable);
        }
    }

    /**
     * sign by MD5withRSA,return base64 encode string values
     *
     * @param data       need sign origin data
     * @param privateKey private key
     * @return java.lang.String
     * @throws SysException
     * @author liguo
     * @date 2019/7/2 15:44
     * @since 1.0.0
     **/
    public static String sign(final String data, final PrivateKey privateKey) {
        try {
            byte[] privateKeyEncoded = privateKey.getEncoded();
            PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(privateKeyEncoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey key = keyFactory.generatePrivate(encodedKeySpec);
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initSign(key);
            signature.update(data.getBytes("utf-8"));
            return Base64.encodeBase64String(signature.sign());
        } catch (Throwable throwable) {
            throw new SysException(SysConsts.SYS_ERROR_CODE, "rsa sign failed", throwable);
        }
    }

    /**
     * sign
     *
     * @param data       need sign origin string values.
     * @param privateKey private key
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/2 16:08
     * @since 1.0.0
     **/
    public static String sign(final String data, final String privateKey) {
        try {
            PrivateKey key = RSAPrivateKeyImpl.parseKey(new DerValue(
                    Base64.decodeBase64(privateKey)));
            return sign(data, key);
        } catch (Throwable throwable) {
            throw new SysException(SysConsts.SYS_ERROR_CODE, "rsa sign failed", throwable);
        }
    }

    /**
     * verify sign
     *
     * @param originData origin data
     * @param signData   signed data
     * @param publicKey  public key must is base64 format string values
     * @return boolean
     * @author liguo
     * @date 2019/7/2 16:09
     * @since 1.0.0
     **/
    public static boolean verify(final String originData, final String signData, final String publicKey) {
        try {
            PublicKey publicKey1 = RSAPublicKeyImpl.parse(new DerValue(Base64.decodeBase64(publicKey)));
            return verify(originData, signData, publicKey1);
        } catch (Throwable throwable) {
            throw new SysException(SysConsts.SYS_ERROR_CODE
                    , "rsa verify sign failed", throwable);
        }
    }

    /**
     * sign verify
     *
     * @param originData origin data
     * @param publicKey  public key must is base64 format string values
     * @param signData   signed data
     * @return boolean if true verify success else failed
     * @throws SysException
     * @author liguo
     * @date 2019/7/2 15:47
     * @since 1.0.0
     **/
    public static boolean verify(final String originData, final String signData, final PublicKey publicKey) {
        try {
            byte[] publicKeyBytes = publicKey.getEncoded();
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey1 = keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initVerify(publicKey1);
            signature.update(originData.getBytes());
            return signature.verify(Base64.decodeBase64(signData.getBytes()));
        } catch (Throwable throwable) {
            throw new SysException(SysConsts.SYS_ERROR_CODE, "rsa verify sign failed", throwable);
        }
    }

    private static byte[] segment(ByteArrayOutputStream outputStream
            , Cipher cipher
            , byte[] decryptData
            , int keyLen
            , boolean isEncryption) throws IllegalBlockSizeException, BadPaddingException {
        int inputLen = decryptData.length;
        int offset = 0, i = 0;
        byte[] cache;
        int maxBlock = keyLen / 8;
        if (isEncryption) {
            maxBlock = keyLen / 8 - 11;
        }
        //segment handle
        System.out.println("--------------------------" + maxBlock + "***********" + keyLen);
        while (inputLen > offset) {
            if (inputLen - offset > maxBlock) {
                System.out.println("-----------------ff-------i=" + i + "    " + (inputLen - offset));
                cache = cipher.doFinal(decryptData, offset, maxBlock);
            } else {
                System.out.println("--------------------------i=" + i + "    " + (inputLen - offset));
                cache = cipher.doFinal(decryptData, offset, inputLen - offset);
            }
            outputStream.write(cache, 0, cache.length);
            i++;
            offset = i * maxBlock;
        }
        return outputStream.toByteArray();
    }


    /**
     * new key pair
     *
     * @param
     * @return java.security.KeyPair
     * @throws SysException
     * @throws NoSuchAlgorithmException
     * @author liguo
     * @date 2019/7/2 14:02
     * @since 1.0.0
     **/
    public static KeyPair newKeyPair() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(512);
            return keyPairGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new SysException(SysConsts.SYS_ERROR_CODE, "new rsa key pair failed", e);
        }
    }

    /**
     * new key pair,return ${KeyPairTuple}
     *
     * @return com.hummer.common.security.Rsa.KeyPairTuple
     * @author liguo
     * @date 2019/7/2 14:16
     * @since 1.0.0
     **/
    public static KeyPairTuple newKeyPair2() {
        KeyPair keyPair = newKeyPair();
        return new KeyPairTuple(new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()))
                , new String(Base64.encodeBase64(keyPair.getPublic().getEncoded())));
    }

    static class KeyPairTuple {
        private String privateKey;
        private String publicKey;

        public static final String DEFAULT_PRIVATE_KEY = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAn7vl+hInDZSUWPo5BknfFNwfkwRo39Y8x4CIq6aImy2Hl2c8TirXAONVD4dr8U40SpuIsQXAOMQS3rB/giiWUQIDAQABAkAzxpkd/OJRwZZwXPM8+Zbo0Q0e/2/rFitvU1d5pCpledKAay8YUjm61E0/0x01/ZTnb5X9Fx0zmpkhj82Et5hRAiEAykAibpm5weCUydidPbuQUB3n9oiPN68sDAcWRHPEGY0CIQDKLzPwHdL48Cy0HTrKFrZ+RVoZVp4CTfGUw+ZbJ2Wk1QIgZKxT91XR3y3ZIjgO3SG2Dgs04cTL9V3ewQXuEZjGbikCIQCxGZPHGni4cbjWtFvQtQB5rsnzM/oITrnz830OD35rWQIgScCHJWraovCblE6RUrNqDVWBqsLrsuYl5bQCZYOTaxU=";
        public static final String DEFAULT_PUBLIC_KEY = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ+75foSJw2UlFj6OQZJ3xTcH5MEaN/WPMeAiKumiJsth5dnPE4q1wDjVQ+Ha/FONEqbiLEFwDjEEt6wf4IollECAwEAAQ==";

        public KeyPairTuple(String privateKey, String publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }

        public String privateKey() {
            return privateKey;
        }

        public String publicKey() {
            return publicKey;
        }

        @Override
        public String toString() {
            return String.format("[\nprivateKey='%s'\npublicKey='%s'\n]", privateKey, publicKey);
        }
    }
}
