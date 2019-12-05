package com.hurry.custom.common.utils;

import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 6/2/2016.
 */
public class TimeHelper {
    private static final String TAG = TimeHelper.class.getCanonicalName();

    public static final String FORMAT_DD_MM_YYYY_HH_MM      = "dd-MM-yyyy hh:mm";
    public static final String EEE_MMM_D_H_MAAA             = "EEE MMM d, h:maaa";
    public static final String YYYY_MM_DD_KK_MM             = "yyyy-MM-dd kk:mm";
    public static final String FORMAT_DD_MM_YYYY_HH_MM_2    = "yyyyMMddhhmmss";
    public static final String YYYY_MM_DD_T_DD_MM_SS        = "yyyy-MM-dd'T'HH:mm:ss";

    public static long getLongValueDate(String inputDate, String dateFormat) {

        String aDateFormat = dateFormat.equalsIgnoreCase("") ? "yyyy-MM-dd HH:mm:ss" : dateFormat;
        SimpleDateFormat outputForm = new SimpleDateFormat(aDateFormat);

        Date startDate = new Date();
        try {
            startDate = outputForm.parse(inputDate);
        }
        catch (ParseException e) {
        }
        return startDate.getTime();
    }


    public static String getDate(TextView mTvCalendarTitle){

        Calendar mThisMonthCalendar = Calendar.getInstance();

        String date = mThisMonthCalendar.get(Calendar.YEAR) + "/" + getTwoDigital(mThisMonthCalendar.get(Calendar.MONTH), false) + "/" + getTwoDigital(mThisMonthCalendar.get(Calendar.DATE), false);
        switch (mThisMonthCalendar.get(Calendar.DAY_OF_WEEK)){
            case 0:
                date  += " Sunday";
                break;
            case 1:
                date  += " Monday";
                break;
            case 2:
                date  += " Tuesday";
                break;
            case 3:
                date  += " Wendnesday";
                break;
            case 4:
                date  += " Thirsday";
                break;
            case 5:
                date  += " Friday";
                break;
            case 6:
                date  += " Saturday";
                break;
        }

        return date;

//        switch(mThisMonthCalendar.get(Calendar.MONTH)){
//            case 0:
//                mTvCalendarTitle.setText( "Janu," + mThisMonthCalendar.get(Calendar.YEAR)); //ary
//                break;
//            case 1:
//                mTvCalendarTitle.setText( "Febu,"+  mThisMonthCalendar.get(Calendar.YEAR));//rary //mThisMonthCalendar.get(Calendar.DATE) +
//                break;
//            case 2:
//                mTvCalendarTitle.setText( "March,"+  mThisMonthCalendar.get(Calendar.YEAR) );
//                break;
//            case 3:
//                mTvCalendarTitle.setText( "April," +   mThisMonthCalendar.get(Calendar.YEAR));
//                break;
//            case 4:
//                mTvCalendarTitle.setText( "May,"+ mThisMonthCalendar.get(Calendar.YEAR) );
//                break;
//            case 5:
//                mTvCalendarTitle.setText( "June,"+   mThisMonthCalendar.get(Calendar.YEAR) );
//                break;
//            case 6:
//                mTvCalendarTitle.setText( "July,"+  mThisMonthCalendar.get(Calendar.YEAR) );
//                break;
//            case 7:
//                mTvCalendarTitle.setText( "August,"+ mThisMonthCalendar.get(Calendar.YEAR) );
//                break;
//            case 8:
//                mTvCalendarTitle.setText( "Supt,"+   mThisMonthCalendar.get(Calendar.YEAR));//ember
//                break;
//            case 9:
//                mTvCalendarTitle.setText( "Octo," + mThisMonthCalendar.get(Calendar.YEAR) );//ber
//                break;
//            case 10:
//                mTvCalendarTitle.setText( "Nove," + mThisMonthCalendar.get(Calendar.YEAR));//mber
//                break;
//            case 11:
//                mTvCalendarTitle.setText( "Dece," + mThisMonthCalendar.get(Calendar.YEAR) );//mber
//                break;
//        }
    }



    public  static String getMonth(int month) {
        int mon  = Math.abs(month % 12);
        if(mon >= 1){
            return new DateFormatSymbols().getMonths()[mon-1];
        }else{
            mon = mon + 12;
        }
        return new DateFormatSymbols().getMonths()[mon-1];
    }


    public static String getTwoDigital(int number, boolean hour){

        String res = String.valueOf(number);
        if(number > 9){
            return res;
        }else{
            res = "0" + res;
        }
        if(hour){
            if(res.equals("00")){
                return "12";
            }
        }

        return res;
    }



    public static String getDate(int year, int month, int day){
       return new StringBuilder().append(day)
                .append("-").append(month + 1).append("-").append(year)
                .append(" ").toString();
    }


    public static String getTime(){
        Calendar calendar = Calendar.getInstance();

        Date dt = new Date();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //int minute  = calendar.get(Calendar.MINUTE);
        int hour = dt.getHours();
        int minute = dt.getMinutes();

        //int hours = new Time(System.currentTimeMillis()).getHours();

        String status = "AM";
        if(hour > 11)
        {
            status = "PM";
        }
        int hour_of_12_hour_format;
        if(hour > 11){
            hour_of_12_hour_format = hour - 12;
        }
        else {
            hour_of_12_hour_format = hour;
        }

        return getTwoDigital(hour_of_12_hour_format, true) + ":" + getTwoDigital(minute, false) + " " + status;

    }


    public static String getTime(int hour, int minute){
        String status = "AM";
        if(hour > 11)
        {
            status = "PM";
        }
        int hour_of_12_hour_format;
        if(hour > 11){
            hour_of_12_hour_format = hour - 12;
        }
        else {
            hour_of_12_hour_format = hour;
        }
        return TimeHelper.getTwoDigital(hour_of_12_hour_format ,  true)+ " : " + TimeHelper.getTwoDigital(minute , false)+ " " + status;
    }
}
