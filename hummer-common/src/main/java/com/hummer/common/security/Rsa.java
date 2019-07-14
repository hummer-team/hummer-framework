package com.hummer.common.security;

import com.hummer.common.exceptions.SysException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static com.hummer.common.SysConstant.SYS_ERROR_CODE;

/**
 * wrapper RSA decrypt encrypt and sign verify
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/7/1 17:53
 * @see <a href='https://www.zhihu.com/question/25912483'>www.zhihu.com/question/25912483</a>
 **/
public class Rsa {
    private static final String CHARSET = "UTF-8";
    private static final String RSA_ALGORITHM = "RSA";
    private static final String RSA_ALGORITHM_SIGN = "SHA256WithRSA";

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    /**
     * init RSA PublicKey privateKey,key format must base64.
     * use {@link #createKeyPair()} or {@link #createKeyTuple()} export key
     *
     * @param base64PublicKey  public key
     * @param privateKey private key
     * @throws SysException
     * @author liguo
     * @date 2019/7/12 13:41
     * @since 1.0.0
     **/
    public Rsa(String base64PublicKey, String base64PrivateKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(base64PublicKey));
            this.publicKey = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(base64PrivateKey));
            this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        } catch (Throwable throwable) {
            throw new SysException(SYS_ERROR_CODE, "init key failed", throwable);
        }
    }

    public Rsa(KeyPairTuple tuple){
       this(tuple.publicKey(),tuple.privateKey());
    }

    public Rsa(KeyPair keyPair){
        this(Base64.encodeBase64String(keyPair.getPublic().getEncoded())
                ,Base64.encodeBase64String(keyPair.getPrivate().getEncoded()));
    }

    /**
     * new key pair
     *
     * @return java.security.KeyPair
     * @throws SysException
     * @author liguo
     * @date 2019/7/12 11:39
     * @since 1.0.0
     **/
    public static KeyPair createKeyPair() {
        try {
            //rsa instance
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            //init KeyPairGenerator key size must 512
            kpg.initialize(512);
            //key pair
            return kpg.generateKeyPair();
        } catch (Throwable throwable) {
            throw new SysException(SYS_ERROR_CODE, "create rsa key pair filed", throwable);
        }
    }

    /**
     * new key pair tuple,public key and private key base 64 encoding
     *
     * @return com.hummer.common.security.Rsa.KeyPairTuple
     * @author liguo
     * @date 2019/7/12 11:38
     * @since 1.0.0
     **/
    public static KeyPairTuple createKeyTuple() {

        KeyPair keyPair = createKeyPair();
        //base 64 encoding  `Base64.encodeBase64URLSafeString`
        String publicKeyStr = Base64.encodeBase64String(keyPair.getPublic().getEncoded());
        String privateKeyStr = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());
        return new KeyPairTuple(privateKeyStr, publicKeyStr);
    }

    /**
     * public key encrypted use base64 format
     *
     * @param data need encrypted body
     * @return java.lang.String
     * @throws SysException
     * @author liguo
     * @date 2019/7/12 11:40
     * @since 1.0.0
     **/
    public String encryptByPublish(String data) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBase64String(segment(cipher
                    , Cipher.ENCRYPT_MODE
                    , data.getBytes(CHARSET)
                    , publicKey.getModulus().bitLength()));
        } catch (Throwable throwable) {
            throw new SysException(SYS_ERROR_CODE, String.format("`%s` encrypted failed"
                    , data), throwable);
        }
    }

    /**
     * decrypt
     *
     * @param data encrypted values
     * @return java.lang.String
     * @throws SysException
     * @author liguo
     * @date 2019/7/12 13:25
     * @since 1.0.0
     **/
    public String decryptByPriavte(String data) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(segment(cipher
                    , Cipher.DECRYPT_MODE
                    , Base64.decodeBase64(data)
                    , privateKey.getModulus().bitLength())
                    , CHARSET);
        } catch (Throwable throwable) {
            throw new SysException(SYS_ERROR_CODE, String.format("`%s` encrypted failed"
                    , data), throwable);
        }
    }

    /**
     * private key encrypted  use base64 format
     *
     * @param data need encrypted body
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/12 13:32
     * @since 1.0.0
     **/
    public String encryptByPrivate(String data) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBase64String(segment(cipher
                    , Cipher.ENCRYPT_MODE
                    , data.getBytes(CHARSET)
                    , publicKey.getModulus().bitLength()));
        } catch (Throwable throwable) {
            throw new SysException(SYS_ERROR_CODE, String.format("`%s` encryptByPrivate failed"
                    , data), throwable);
        }
    }

    /**
     * decrypt
     *
     * @param data encrypted value,format must base64
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/12 13:48
     * @since 1.0.0
     **/
    public String decryptByPublic(String data) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(segment(cipher, Cipher.DECRYPT_MODE,
                    Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
        } catch (Throwable throwable) {
            throw new SysException(SYS_ERROR_CODE, String.format("`%s` decryptByPublic failed"
                    , data), throwable);
        }
    }

    /**
     * sign
     *
     * @param data origin  values
     * @return java.lang.String
     * @author liguo
     * @date 2019/7/12 13:49
     * @since 1.0.0
     **/
    public String sign(String data) {
        try {
            //sign
            Signature signature = Signature.getInstance(RSA_ALGORITHM_SIGN);
            signature.initSign(privateKey);
            signature.update(data.getBytes(CHARSET));
            return Base64.encodeBase64String(signature.sign());
        } catch (Throwable throwable) {
            throw new SysException(SYS_ERROR_CODE, String.format("`%s` sign failed"
                    , data), throwable);
        }
    }

    /**
     * sign verified
     *
     * @param data verified data
     * @param sign signed value
     * @return boolean
     * @author liguo
     * @date 2019/7/12 13:50
     * @since 1.0.0
     **/
    public boolean verify(String data, String sign) {
        try {
            Signature signature = Signature.getInstance(RSA_ALGORITHM_SIGN);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(CHARSET));
            return signature.verify(Base64.decodeBase64(sign));
        } catch (Throwable throwable) {
            throw new SysException(SYS_ERROR_CODE, String.format("`%s` verify sign failed"
                    , data), throwable);
        }
    }

    private static byte[] segment(Cipher cipher, int mode, byte[] data, int keySize) {
        int maxBlock = mode == Cipher.DECRYPT_MODE ? keySize / 8 : keySize / 8 - 11;
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            while (data.length > offSet) {
                if (data.length - offSet > maxBlock) {
                    buff = cipher.doFinal(data, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(data, offSet, data.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
            return out.toByteArray();
        } catch (Throwable throwable) {
            throw new SysException(SYS_ERROR_CODE, "rsa segment exception", throwable);
        }
    }

    static class KeyPairTuple {
        private String privateKey;
        private String publicKey;

        public static final String DEFAULT_PRIVATE_KEY = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAni+GuG36davqnCWlzvF3QzRF8B3akrVwChj1Ywt+Y5WG1BJtgcXh+GfVPcL4Y3vtZN/wJYrrKcHSfwhDsynPMwIDAQABAkB2W4ElxcQ8/2EPbGvfp3Rg6F/cBbphQziNlZclgJgU0cKmgf6eolKgT8bWZNhivKyiMpU+SZTMT1a02pcqdZEhAiEA2AUBtx4rxqqwPMvTKxErn90QnAQA5H/jcPT0YxBVaLcCIQC7dlpBUV3VNTA9IorsqySOm0wlnvIGt2EHHdtugc95ZQIhAIm5D3HnG3PK6Repv5UKmmyOrYM6jjMgUip3EcSC6mEbAiA55npGBm2m9sCpgUvLgajO6yR/0jIK5QTw/8XQwgNlCQIgKpNxXRH32phL06kv2qiEW2YleiAA6put3ZrYRlblyE4=";
        public static final String DEFAULT_PUBLIC_KEY = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4vhrht+nWr6pwlpc7xd0M0RfAd2pK1cAoY9WMLfmOVhtQSbYHF4fhn1T3C+GN77WTf8CWK6ynB0n8IQ7MpzzMCAwEAAQ==";

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
