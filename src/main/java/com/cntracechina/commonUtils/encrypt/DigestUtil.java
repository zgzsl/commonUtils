package com.cntracechina.commonUtils.encrypt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName DigestUtil
 * @Description 加密转码工具
 * @Author chenxw
 * @Date 2019/5/14 12:03
 * @Version 1.0
 **/
public class DigestUtil {

    public static String md5Degist (String source) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] bytes = MessageDigest.getInstance("MD5").digest(source.getBytes("utf-8"));
        return byte2hex(bytes);
    }

    private static String byte2hex(byte[] bytes) {
        StringBuffer sign = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

}
