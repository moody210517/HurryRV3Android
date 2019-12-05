package com.hurry.custom.model;

/**
 * Created by Administrator on 3/24/2017.
 */

public class QuoteCoperationModel {

    public String name;
    public String address;
    public String phone;
    public String description;
    public String loadType;

    public String orderId;
    public String trackId;
    public String state;
    public boolean selection;
    public boolean service_choose;
    public String quote_id;


    public ServiceModel expeditedService = new ServiceModel();
    public ServiceModel expressService = new ServiceModel();
    public ServiceModel economyService = new ServiceModel();

    public AddressModel addressModel = new AddressModel();
    public ServiceModel serviceModel = new ServiceModel();
    public DateModel dateModel = new DateModel();

}
