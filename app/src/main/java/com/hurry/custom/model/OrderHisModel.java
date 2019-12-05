package com.hurry.custom.model;

/**
 * Created by Administrator on 3/21/2017.
 */

public class OrderHisModel {

    public String paymentId;
    public String orderId;
    public String trackId;
    public String state;
    public String is_quote_request;
    public String payment;
    public String accepted_by;
    public String new_date;
    public String new_time;
    public String price;

    public AddressModel addressModel = new AddressModel();
    public ServiceModel serviceModel = new ServiceModel();
    public DateModel dateModel = new DateModel();
    public OrderModel orderModel = new OrderModel();
}
