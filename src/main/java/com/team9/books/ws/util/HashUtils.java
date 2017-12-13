package com.team9.books.ws.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public final class HashUtils {
    private HashUtils() {}

    public static String getHashNumber(Validity v) {
        Calendar now = new GregorianCalendar();
        StringBuilder sb = new StringBuilder();
        switch (v) {
            case DAILY:
                sb.append(now.get(Calendar.DAY_OF_YEAR)).append('-');
            case WEEKLY:
                sb.append(now.get(Calendar.WEEK_OF_YEAR)).append('-');
            case MONTHLY:
                sb.append(now.get(Calendar.MONTH)).append('-');
            case YEARLY:
                sb.append(now.get(Calendar.YEAR)).append('-');
            default:
                sb.append('0');
        }

        return sb.toString();
    }

    public static String hashString(String message, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashedBytes = digest.digest(message.getBytes("UTF-8"));
            return convertByteArrayToHexString(hashedBytes);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    public enum Validity { FOREVER, YEARLY, MONTHLY, WEEKLY, DAILY}
}
