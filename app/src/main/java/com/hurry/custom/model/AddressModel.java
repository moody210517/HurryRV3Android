package com.hurry.custom.model;

/**
 * Created by Administrator on 3/20/2017.
 */

public class AddressModel implements  Cloneable{


    public AddressModel clone() throws CloneNotSupportedException {
        return (AddressModel) super.clone();
    }

    public String senderName;
    public String sourceAddress;
    public String sourceArea = "";
    public String sourceCity = "";
    public String sourceState = "";
    public String sourcePinCode = "";
    public String sourcePhonoe;
    public String sourceLandMark;
    public String sourceInstruction;

    public String desAddress;
    public String desArea = "";
    public String desCity = "";
    public String desState = "";
    public String desPinCode = "";
    public String desLandMark;
    public String desInstruction;
    public String desPhone;
    public String desName;

    public double sourceLat = 0;
    public double sourceLng = 0;

    public double desLat = 0;
    public double desLng = 0;

    public String duration = "";
    public String distance = "";
    public String distance_text;
}
