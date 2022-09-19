package com.shanlu.util;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;

public class MyDateUtil {

    public static String getTimestamp(String dateString){
        if(dateString.isEmpty() || dateString == null){
            return "";
        }
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
//        String format = sdf.format(new Date(Long.valueOf(dateString)));
//        System.out.println(format);
        try {
            DateUtils.parseDate(dateString,"yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCurrentTimeStamp(){
        long time = System.currentTimeMillis();
        String secondsTime = String.valueOf(time);
        return secondsTime;
    }

    public static String getStrateTimeStamp(String dateString){
        if(dateString.isEmpty() || dateString == null){
            return "";
        }
        try {
            dateString = dateString+" 00:00:00";
            long time = DateUtils.parseDate(dateString, "yyyy-MM-dd HH:mm:ss").getTime();
            String startTimeStamp = String.valueOf(time);
            return startTimeStamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String getEndTimeStamp(String dateString){
        if(dateString.isEmpty() || dateString == null){
            return "";
        }
        try {
            dateString = dateString+" 23:59:59";
            long time = DateUtils.parseDate(dateString, "yyyy-MM-dd HH:mm:ss").getTime();
            String endTimeStamp = String.valueOf(time);
            return endTimeStamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        String currentTimeStamp = getCurrentTimeStamp();
        System.out.println(currentTimeStamp);
    }
}
