package com.hurry.custom.model;

/**
 * Created by Administrator on 3/21/2017.
 */

public class OrderRescheduleModel {

    public String orderId;
    public String trackId;
    public String payment;

    public String new_date;
    public String new_time;
    public AddressModel addressModel = new AddressModel();
    public ServiceModel serviceModel = new ServiceModel();
    public DateModel dateModel = new DateModel();
    public OrderModel orderModel = new OrderModel();
}
