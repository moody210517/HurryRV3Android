package com.hurry.custom.model;

/**
 * Created by Administrator on 3/21/2017.
 */

public class QuoteModel {

    public String orderId;
    public String trackId;
    public String weight;
    public String quote_id;
    public String payment;
    public boolean selection;
    public boolean service_choose;


    public int option;
    public AddressModel addressModel = new AddressModel();
    public ServiceModel serviceModel = new ServiceModel();
    public DateModel dateModel = new DateModel();
    public OrderModel orderModel = new OrderModel();
}
