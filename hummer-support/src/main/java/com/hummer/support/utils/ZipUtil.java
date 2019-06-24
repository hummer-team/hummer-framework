package com.hummer.support.utils;


import com.hummer.support.SysConsts;
import com.hummer.support.exceptions.SysException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * this class wrapper zip,unzip feature
 */
public class ZipUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZipUtil.class);
    private static final String STR = ".";
    private static final String COMPRESSED_STRING_NOT_NULL = "The 'compressedStr' must not be null!";

    private ZipUtil() {

    }

    /**
     * gzip
     *
     * @param primStr
     * @return
     */
    public static String gzip(String primStr) {
        Assert.notNull(primStr, "The 'primStr' must not be null!");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(primStr.getBytes(SysConsts.DEFAULT_CHARSET));
        } catch (IOException e) {
            throw new SysException(SysConsts.SYS_ERROR_CODE, e.getMessage(), e);
        }
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    /**
     * un gzip
     *
     * @param compressedStr compressed string value
     * @return
     */
    public static String gunzip(String compressedStr) {

        Assert.notNull(compressedStr, COMPRESSED_STRING_NOT_NULL);

        byte[] compressed = Base64.getDecoder().decode(compressedStr);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ByteArrayInputStream in = new ByteArrayInputStream(compressed);
             GZIPInputStream ginzip = new GZIPInputStream(in)) {

            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
        } catch (IOException e) {
            throw new SysException(SysConsts.SYS_ERROR_CODE, e.getMessage(), e);
        }

        return new String(out.toByteArray(), SysConsts.DEFAULT_CHARSET);
    }

    /**
     * gzip
     *
     * @param str target values
     * @return after compress  string
     */
    public static final String zip(String str) {
        Assert.notNull(str, "The 'str' must not be null!");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (ZipOutputStream zout = new ZipOutputStream(out)) {
            zout.putNextEntry(new ZipEntry("0"));
            zout.write(str.getBytes(SysConsts.DEFAULT_CHARSET));
        } catch (IOException e) {
            throw new SysException(SysConsts.SYS_ERROR_CODE, e.getMessage(), e);
        }
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    /**
     * gzip
     *
     * @param val target bytes
     * @return byte[] after compress bytes
     * @author liguo
     * @date 2019/6/24 16:10
     * @version 1.0.0
     **/
    public static byte[] gzip(byte[] val) throws IOException {
        byte[] result = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(val.length);
             GZIPOutputStream gos = new GZIPOutputStream(bos);) {
            gos.write(val, 0, val.length);
            gos.finish();
            gos.flush();
            bos.flush();
            result = bos.toByteArray();
        }
        return result;
    }

    /**
     * 使用zip进行解压缩
     *
     * @param compressedStr 压缩后的文本
     * @return 解压后的字符串
     */
    public static String unzip(String compressedStr) {
        Assert.notNull(compressedStr, COMPRESSED_STRING_NOT_NULL);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] compressed = Base64.getDecoder().decode(compressedStr);
        try (ByteArrayInputStream in = new ByteArrayInputStream(compressed);
             ZipInputStream zin = new ZipInputStream(in)) {
            zin.getNextEntry();
            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = zin.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
        } catch (IOException e) {
            throw new SysException(SysConsts.SYS_ERROR_CODE, e.getMessage(), e);
        }
        return new String(out.toByteArray(), SysConsts.DEFAULT_CHARSET);
    }

}
