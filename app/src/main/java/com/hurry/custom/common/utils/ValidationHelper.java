package com.hurry.custom.common.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 6/2/2016.
 */
public class ValidationHelper {
    public static boolean isValidPhoneNumber(CharSequence phoneNumber) {

        boolean res = (!TextUtils.isEmpty(phoneNumber)) ? Patterns.PHONE.matcher(phoneNumber).matches() : false;
        if(res == false){
            return false;
        }
        return true;
//        if(phoneNumber.length() == 13){
//            return true;
//        }
//        return false;
        //
    }

    public static boolean isValidEmail(String email) {
        return email.contains("@");
    }

    public static boolean isValidEmailDetails(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }


    public static boolean isValidPassword(String password) {
        return password.length() > 0;
    }

    public static boolean isPhoneValid(String phone){
        Pattern pattern = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
        Matcher matcher = pattern.matcher(phone);

        if(matcher.matches())
            return true;
        else
            return false;
    }

    public static boolean isPostCode(String postcode){
        if(postcode.length() == 6){
            return  true;
        }
        return true;
    }

    public static boolean isSpecialCharacter(String string)
    {
        boolean flag = false;
        for(int k = 0; k < string.length(); k++){
            Character c = string.charAt(k);
            if(c.toString().matches("[^a-z A-Z]")){//0-9
                flag = true;
             //   break;
            }
        }
        return flag;
    }

    public static boolean checkStringAndDigital(String myData){
        for(int k = 0; k < myData.length(); k++){
            String c = myData.substring(k, k + 1);
            char[] character = c.toCharArray();
            if(containsSpecialCharacter(c) || Character.isDigit(character[0])){
                return true;
            }
        }
        return false;
    }


    public static  boolean containsSpecialCharacter(String str){
        String specialCharacters = "[" + "-/@#!*$%^&.'_+={}():;,?" + "]+" ;
        boolean flag =  str.toString().matches(specialCharacters);
        return flag;
    }


}
