package com.hurry.custom.model;

/**
 * Created by Administrator on 3/21/2017.
 */

public class OrderCorporateHisModel {

    public String orderId;
    public String trackId;
    public String state;
    public String payment;
    public String accepted_by;
    public String deliver;
    public String loadType;

    public String receiver_signature;

    public AddressModel addressModel = new AddressModel();
    public ServiceModel serviceModel = new ServiceModel();
    public DateModel dateModel = new DateModel();
    public CarrierModel carrierModel = new CarrierModel();
}
