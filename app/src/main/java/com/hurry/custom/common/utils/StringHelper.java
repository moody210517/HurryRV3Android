package com.hurry.custom.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Administrator on 6/2/2016.
 */
public class StringHelper {
    public static final String BLANK = "";
    public static final String NULL  = "null";
    public static final String SPAC  = "[|]";
    public static final String SPAC2 = "[/]";


    private static final char[] symbols;
    private static final Random mRandom = new Random();
    private static char[] mChBuffer     = null;
    private static int mToggleWaiting   = 0;

    static {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch)
            tmp.append(ch);
        for (char ch = 'A'; ch <= 'Z'; ++ch)
            tmp.append(ch);
        symbols = tmp.toString().toCharArray();
    }


    public static String md5(String s) {
        try { // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e) {
            return BLANK;
        }
    }

    public static String decodeString(String str) {
        String decodedString = BLANK;
        try {
            decodedString = URLDecoder.decode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
        }
        return decodedString;
    }

    public static String encodeString(String str) {
        String encodedString = BLANK;
        try {
            encodedString = URLEncoder.encode(str, "UTF-8");
            encodedString = encodedString.replace("+", "%20");
        }
        catch (UnsupportedEncodingException e) {
            encodedString = str;
        }
        return encodedString;
    }

    public static boolean isEmpty(String str) {
        return (str == null || BLANK.equals(str)) ? true : false;
    }

    public static boolean isEmpty(CharSequence target) {
        return (target == null || isEmpty(target.toString())) ? true : false;
    }

    public static boolean isNull(String str) {
        return (str == null || BLANK.equals(str) || NULL.equals(str)) ? true : false;
    }

    public static boolean isNull(CharSequence target) {
        return (target == null || isNull(target.toString())) ? true : false;
    }

    public static boolean isValidEmail(CharSequence target) {
        return target == null || target.toString().contains("@");
    }

    public static boolean isValidPassword(CharSequence target) {
        return target != null && target.toString().length() > 4;
    }

    public static String createId() {
        return UUID.randomUUID().toString();
    }

    public static boolean checkIfDataIsUrlAndCreateIntent(String data) {
        boolean barcodeDataIsUrl;
        try {
            @SuppressWarnings("unused")
            URL url = new URL(data);
            barcodeDataIsUrl = true;
        }
        catch (MalformedURLException exc) {
            barcodeDataIsUrl = false;
        }
        return barcodeDataIsUrl;
    }

    public static String[] split(String data, String split) {
        return (data == null) ? null : data.split(split);
    }

    public static boolean contains(String data, String contain) {
        return (data == null) ? false : data.contains(contain);
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static String formatNumber(long amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    public static String generateRandomCode(int length) {
        if (mChBuffer == null || mChBuffer.length != length)
            mChBuffer = new char[length];
        for (int i = 0; i < mChBuffer.length; i ++) {
            mChBuffer[i] = symbols[mRandom.nextInt(symbols.length)];
        }
        return new String(mChBuffer);
    }

    public static String waitingBar() {
        mToggleWaiting ++;
        if (mToggleWaiting == 1)
            return ".    ";
        else if (mToggleWaiting == 2)
            return "..   ";
        else if (mToggleWaiting == 3)
            return "...  ";
        else if (mToggleWaiting == 4)
            return ".... ";
        else if (mToggleWaiting == 5) {
            mToggleWaiting = 0;
            return ".....";
        }
        return "";
    }


//    public static ArrayList<String> cloneList(ArrayList<String> arrayList) {
//        ArrayList<String> clone = new ArrayList<String>(arrayList.size());
//        for (String item : arrayList)
//            try {
//                clone.add(item.clone());
//            } catch (CloneNotSupportedException e) {
//                e.printStackTrace();
//            }
//        return clone;
//    }


}
