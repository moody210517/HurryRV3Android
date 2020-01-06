package com.hurry.custom.model;

import com.hurry.custom.common.Constants;

/**
 * Created by Administrator on 4/5/2017.
 */

public class PriceType {

    public String expedited_name = "Expedited";
    public    String expeditied_price;
    private String expedited_duration = "In 4 hours";
    public int expedited = 4;

    public String express_name = "Express";
    public  String express_price;
    private String express_duration = "In 8 hours";
    public int express = 8;

    public String economy_name = "Economy";
    public String  economy_price;
    private String economy_duraiton = "In 16 hours";
    public  int economy = 16;

    public double distance = 0;


    public String getDuration(int type , int delivery_type){
        if(delivery_type == Constants.SAME_CITY){
            if(type == 1){
                return "In 4 hours";
            }else if(type == 2){
                return "In 8 hours";
            }else if(type == 3){
                return  "In 16 hours";
            }
        }else if(delivery_type == Constants.OUT_STATION){
            if(type == 1){
                return "In 1-2 days";
            }else if(type == 2){
                return "In 2 to 3 days";
            }else if(type == 3){
                return  "In 3 to 5 days";
            }
        }else if(delivery_type == Constants.INTERNATIONAL){
            if(type == 1){
                return "In 2 to 3 days";
            }else if(type == 2){
                return "In 2 to 3 days";
            }else if(type == 3){
                return  "In 2 to 3 days";
            }
        }

        return "";
    }

}
