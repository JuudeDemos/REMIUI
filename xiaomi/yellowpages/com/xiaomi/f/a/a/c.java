package com.xiaomi.f.a.a;

import com.alipay.mobilesecuritysdk.constant.ConfigConstant;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class c {
    public static String a(int i) {
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i2 = 0; i2 < i; i2++) {
            stringBuffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".length())));
        }
        return stringBuffer.toString();
    }

    public static String a(String str) {
        try {
            MessageDigest.getInstance("SHA1").update(ak(str));
            str = String.format("%1$032X", new Object[]{new BigInteger(1, r0.digest())});
        } catch (NoSuchAlgorithmException e) {
        }
        return str;
    }

    public static String a(Collection collection, String str) {
        return collection == null ? null : a(collection.iterator(), str);
    }

    public static String a(Iterator it, String str) {
        if (it == null) {
            return null;
        }
        if (!it.hasNext()) {
            return ConfigConstant.WIRELESS_FILENAME;
        }
        Object next = it.next();
        if (!it.hasNext()) {
            return next.toString();
        }
        StringBuffer stringBuffer = new StringBuffer(256);
        if (next != null) {
            stringBuffer.append(next);
        }
        while (it.hasNext()) {
            if (str != null) {
                stringBuffer.append(str);
            }
            next = it.next();
            if (next != null) {
                stringBuffer.append(next);
            }
        }
        return stringBuffer.toString();
    }

    public static byte[] ak(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return str.getBytes();
        }
    }
}
