package com.hurry.custom.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.model.OrderHisModel;
import com.hurry.custom.model.OrderModel;
import com.hurry.custom.view.activity.HomeActivity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 7/21/2016.
 */
 public class GetOrderHisProcess extends AsyncTask<Void, Void, Void> {
    Context  mContext;
    String result;
    int index;

    public GetOrderHisProcess(Context con, int index)
    {
        super();
        this.mContext = con;
        this.index = index;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... args) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        ServiceHandler sh = new ServiceHandler();
        params.add(new BasicNameValuePair("user_id", PreferenceUtils.getUserId(mContext)));
        result = sh.makeServiceCall( Constants.BASE_URL_ORDER + "get_orders_his", ServiceHandler.POST, params); //
        return null;
    }


    @Override
    protected void onPostExecute(Void ress) {

         if (result != null) {
             try {
                 JSONObject response = new JSONObject(result);
                 if (response != null) {

                     String result = response.getString("result");
                     if(result.equals("400")){
                         if(mContext instanceof HomeActivity){
                             ((HomeActivity)mContext).setUpPersonalOrderHis(index);
                         }
                         return;
                     }

                     JSONArray jsonArray = response.getJSONArray("orders");
                     Constants.orderHisModels.clear();

                     for (int k= 0; k < jsonArray.length() ; k++){

                         JSONObject object = jsonArray.getJSONObject(k);
                         OrderHisModel orderHisModel = new OrderHisModel();
                         orderHisModel.orderId = object.getString("id");
                         orderHisModel.trackId = object.getString("track");
                         orderHisModel.payment = object.getString("payment");
                         orderHisModel.accepted_by = object.getString("accepted_by");

                         orderHisModel.dateModel.date = object.getString("date");
                         orderHisModel.dateModel.time = object.getString("time");

                         orderHisModel.serviceModel.name = object.getString("service_name");
                         orderHisModel.serviceModel.price = object.getString("service_price");
                         orderHisModel.serviceModel.time_in = object.getString("service_timein");

                         orderHisModel.state = object.getString("state");
                         orderHisModel.is_quote_request = object.getString("is_quote_request");
                         orderHisModel.price = object.getString("price");
                         orderHisModel.paymentId = object.getString("transaction_id");

                         try{
                             JSONArray addressArray = object.getJSONArray("address");
                             for(int i = 0; i < addressArray.length(); i++){
                                 JSONObject addressObj = addressArray.getJSONObject(i);
                                 orderHisModel.addressModel.sourceAddress = addressObj.getString("s_address");
                                 orderHisModel.addressModel.sourceArea =  addressObj.getString("s_area");
                                 orderHisModel.addressModel.sourceCity = addressObj.getString("s_city");
                                 orderHisModel.addressModel.sourceState = addressObj.getString("s_state");
                                 orderHisModel.addressModel.sourcePinCode = addressObj.getString("s_pincode");
                                 try{
                                     String res = addressObj.getString("s_phone");
                                     orderHisModel.addressModel.sourcePhonoe = res;
                                     String[] resArray = res.split(":");
                                     if(resArray.length == 2){
                                         orderHisModel.addressModel.sourcePhonoe = resArray[0];
                                         orderHisModel.addressModel.senderName = resArray[1];
                                     }
                                 }catch (Exception e){};
                                 //orderHisModel.addressModel.sourcePhonoe = addressObj.getString("s_phone");
                                 orderHisModel.addressModel.sourceLandMark = addressObj.getString("s_landmark");
                                 orderHisModel.addressModel.sourceInstruction = addressObj.getString("s_instruction");
                                 orderHisModel.addressModel.sourceLat = addressObj.getDouble("s_lat");
                                 orderHisModel.addressModel.sourceLng = addressObj.getDouble("s_lng");

                                 orderHisModel.addressModel.desAddress = addressObj.getString("d_address");
                                 orderHisModel.addressModel.desArea = addressObj.getString("d_area");
                                 orderHisModel.addressModel.desCity = addressObj.getString("d_city");
                                 orderHisModel.addressModel.desState = addressObj.getString("d_state");
                                 orderHisModel.addressModel.desPinCode = addressObj.getString("d_pincode");
                                 orderHisModel.addressModel.desLandMark = addressObj.getString("d_landmark");
                                 orderHisModel.addressModel.desInstruction = addressObj.getString("d_instruction");
                                 orderHisModel.addressModel.desLat = addressObj.getDouble("d_lat");
                                 orderHisModel.addressModel.desLng = addressObj.getDouble("d_lng");
                                 orderHisModel.addressModel.desPhone = addressObj.getString("d_phone");
                                 orderHisModel.addressModel.desName = addressObj.getString("d_name");
                             }

                         }catch (Exception e){};


                         try{
                             JSONArray productArray = object.getJSONArray("products");
                             orderHisModel.orderModel = new OrderModel();
                             orderHisModel.orderModel.itemModels = new ArrayList<ItemModel>();

                             for(int j = 0; j < productArray.length(); j++){
                                 JSONObject productObj = productArray.getJSONObject(j);
                                 int product_type = productObj.getInt("product_type");

                                 if(product_type == Constants.CAMERA_OPTION){
                                     orderHisModel.orderModel.product_type = product_type;

                                     ItemModel model = new ItemModel();
                                     model.image = productObj.getString("image");
                                     model.quantity  = productObj.getString("quantity");
                                     model.weight = productObj.getString("weight");

                                     orderHisModel.orderModel.itemModels.add(model);

                                 }else if(product_type == Constants.ITEM_OPTION){
                                     orderHisModel.orderModel.product_type = product_type;
                                     ItemModel model = new ItemModel();
                                     model.title = productObj.getString("title");

                                     model.quantity  = productObj.getString("quantity");
                                     model.weight = productObj.getString("weight");
                                     try{
                                         String[] dim = productObj.getString("dimension").split("X");
                                         model.dimension1 = dim[0];
                                         model.dimension2 = dim[1];
                                         model.dimension3 = dim[2];
                                     }catch (Exception e){};
                                     orderHisModel.orderModel.itemModels.add(model);

                                 }else if(product_type == Constants.PACKAGE_OPTION){
                                     orderHisModel.orderModel.product_type = product_type;
                                     ItemModel model = new ItemModel();
                                     model.title = productObj.getString("title");
                                     model.quantity  = productObj.getString("quantity");
                                     model.weight = productObj.getString("weight");
                                     orderHisModel.orderModel.itemModels.add(model);
                                 }
                             }
                         }catch (Exception e){
                             String err  = e.toString();
                         }
                         Constants.orderHisModels.add(orderHisModel);
                     }


                     if(mContext instanceof HomeActivity){
                         ((HomeActivity)mContext).setUpPersonalOrderHis(index);
                     }

                 }else if(response.getString("result").equals("400")){
                     Toast.makeText(mContext , mContext.getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                 }

             } catch (JSONException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
        }
    }

}
