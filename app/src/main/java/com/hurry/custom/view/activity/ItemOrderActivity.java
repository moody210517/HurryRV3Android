package com.hurry.custom.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.view.BaseActivity;
import com.hurry.custom.view.BaseBackActivity;
import com.hurry.custom.view.adapter.CustomerAdapter;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2/12/2018.
 */

public class ItemOrderActivity extends BaseBackActivity implements View.OnClickListener{


    LinearLayout linItem;
    TextView txtMore;
    Button btnContinue;
    @BindView(R.id.scrollview) ScrollView scrollView;
    public static ItemOrderActivity itemOrderActivity;
    @BindView(R.id.rl_station) RelativeLayout rlStation;
    @BindView(R.id.rl_city) RelativeLayout rlCity;
    @BindView(R.id.rl_international) RelativeLayout rlInternational;
    @BindView(R.id.txt_header) TextView txtHeader;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_item_order);
        ButterKnife.bind(this);
        itemOrderActivity = this;
        initBackButton(toolbar, getString(R.string.select_the_item));

        Constants.page_type = "item";

        if(Constants.itemOrderModel.itemModels.size() == 0){
            Constants.itemOrderModel.itemModels.clear();
            ItemModel itemModel = new ItemModel();
            itemModel.title = "";
            itemModel.dimension1 = "";
            itemModel.dimension2 = "";
            itemModel.dimension3 = "";
            itemModel.weight = Constants.weight.get(0);
            itemModel.weight_value = Constants.weight_value.get(0);
            itemModel.quantity = Constants.quantity.get(0);
            Constants.itemOrderModel.itemModels.add(itemModel);
        }
        initView();
        updateView();

    }


    private void initView(){
        linItem = (LinearLayout)findViewById(R.id.lin_item);
        txtMore = (TextView)findViewById(R.id.txt_add_more);
        txtMore.setOnClickListener(this);
        btnContinue = (Button)findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(this);

        if(getValue("type").equals("station")){
            rlStation.setBackgroundResource(R.drawable.ic_triangle_right_teal);
            txtHeader.setText(getString(R.string.out_station));
        }else if(getValue("type").equals("city")){
            rlCity.setBackgroundResource(R.drawable.ic_triangle_left_teal);
            txtHeader.setText(getString(R.string.same_city));
        }else{
            rlInternational.setBackgroundResource(R.drawable.ic_triangle_right_teal);
            txtHeader.setText(getString(R.string.international_delivery));
        }
    }

    private  void updateView(){

        if(Constants.itemOrderModel != null && Constants.itemOrderModel.itemModels.size() == 10){
            txtMore.setVisibility(View.GONE);
        }else{
            txtMore.setVisibility(View.VISIBLE);
        }

        linItem.removeAllViews();
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int k = 0; k < Constants.itemOrderModel.itemModels.size() ; k++){

            ItemModel itemModel = Constants.itemOrderModel.itemModels.get(k);

            View v = layoutInflater.inflate(R.layout.row_product_item, null);

            TextView txtRemove = (TextView)v.findViewById(R.id.txt_remove);
            final int finalK1 = k;
            txtRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Constants.itemOrderModel.itemModels.size() > finalK1 && Constants.itemOrderModel.itemModels.size() > 1){
                        ItemModel model = Constants.itemOrderModel.itemModels.get(finalK1);
                        Constants.itemOrderModel.itemModels.remove(model);
                        updateView();

                    }
                }
            });
            LinearLayout linRemove = (LinearLayout)v.findViewById(R.id.lin_remove);
            linRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Constants.itemOrderModel.itemModels.size() > finalK1 && Constants.itemOrderModel.itemModels.size() > 1){
                        ItemModel model = Constants.itemOrderModel.itemModels.get(finalK1);
                        Constants.itemOrderModel.itemModels.remove(model);
                        updateView();

                    }
                }
            });


            final CheckBox chkPackage = (CheckBox)v.findViewById(R.id.chk_package);
            chkPackage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(chkPackage.isChecked()){
                        Constants.itemOrderModel.itemModels.get(finalK1).mPackage = "1";
                    }else{
                        Constants.itemOrderModel.itemModels.get(finalK1).mPackage = "0";
                    }
                }
            });

            final AutoCompleteTextView edtItem = (AutoCompleteTextView)v.findViewById(R.id.edt_item);
            String [] itemLists = new String[Constants.itemLists.size()];
            ArrayList<String> itemArray = new ArrayList<>();
            for(int j = 0 ; j < Constants.itemLists.size(); j++){
                itemLists[j] = Constants.itemLists.get(j).title;
                itemArray.add(Constants.itemLists.get(j).title);
            }


            CustomerAdapter cityAdapter = new CustomerAdapter(this , R.layout.custom_auto, itemArray);

            edtItem.setAdapter(cityAdapter);
            edtItem.setThreshold(1);//will start working from first character

            final EditText edtDimension1 = (EditText)v.findViewById(R.id.edt_dimension1);
            final EditText edtDimension2 = (EditText)v.findViewById(R.id.edt_dimension2);
            final EditText edtDimension3 = (EditText)v.findViewById(R.id.edt_dimension3);

            edtItem.setText(itemModel.title);

            edtDimension1.setText(itemModel.dimension1);
            edtDimension2.setText(itemModel.dimension2);
            edtDimension3.setText(itemModel.dimension3);

            final NiceSpinner spQuantity = (NiceSpinner)v.findViewById(R.id.sp_quantity);
            //spQuantity.type = 1;
            spQuantity.setPadding(20, 10,10,10);

            //spQuantity.setBackgroundColor(getResources().getColor(R.color.light_mustard_color));
            final NiceSpinner spWeight = (NiceSpinner)v.findViewById(R.id.sp_weight);
            //spWeight.type = 1;
            spWeight.setPadding(20, 10,10,10);

            //spWeight.setBackgroundColor(getResources().getColor(R.color.light_mustard_color));

            spQuantity.attachDataSource(Constants.quantity);
            spWeight.attachDataSource(Constants.weight);

            spQuantity.setText(itemModel.quantity);
            spWeight.setText(itemModel.weight);

            if(itemModel.mPackage.equals("1")){
                chkPackage.setChecked(true);
        }

            final int finalK = k;
            spQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Constants.itemOrderModel.itemModels.get(finalK).quantity = Constants.quantity.get(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            spWeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Constants.itemOrderModel.itemModels.get(finalK).weight = Constants.weight.get(position);
                    Constants.itemOrderModel.itemModels.get(finalK).weight_value = Constants.weight_value.get(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            edtItem.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Constants.itemOrderModel.itemModels.get(finalK).title = edtItem.getText().toString().trim();
                }
            });

            edtDimension1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Constants.itemOrderModel.itemModels.get(finalK).dimension1 = edtDimension1.getText().toString().trim();
                }
            });
            edtDimension2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Constants.itemOrderModel.itemModels.get(finalK).dimension2 = edtDimension2.getText().toString().trim();
                }
            });
            edtDimension3.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    Constants.itemOrderModel.itemModels.get(finalK).dimension3 = edtDimension3.getText().toString().trim();
                }
            });
            linItem.addView(v);
        }

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                if(Constants.itemOrderModel.itemModels.size() > 2){
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }else{
                    scrollView.fullScroll(ScrollView.SCROLL_INDICATOR_TOP);
                }

            }
        });

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_add_more:
                if(Constants.itemOrderModel.itemModels.size() < 10){
                    ItemModel itemModel = new ItemModel();
                    itemModel.title = "";
                    itemModel.dimension1 = "";
                    itemModel.dimension2 = "";
                    itemModel.dimension3 = "";
                    itemModel.weight = Constants.weight.get(0);
                    itemModel.weight_value = Constants.weight_value.get(0);
                    itemModel.quantity = Constants.quantity.get(0);
                    Constants.itemOrderModel.itemModels.add(itemModel);

                    updateView();
                }else{
                    Toast.makeText(this, "Can not upload more", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_continue:
                if(checkInput()){
                    Intent intent = new Intent(this, AddressDetailsNewActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, getResources().getString(R.string.input_all), Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private boolean checkInput(){

        if(Constants.itemOrderModel.itemModels.size() == 0){
            return false;
        }
        for(int k = 0; k < Constants.itemOrderModel.itemModels.size(); k++){
            String title =  Constants.itemOrderModel.itemModels.get(k).title;
            String dimension1 = Constants.itemOrderModel.itemModels.get(k).dimension1;
            String dimension2 = Constants.itemOrderModel.itemModels.get(k).dimension2;
            String dimension3 = Constants.itemOrderModel.itemModels.get(k).dimension3;
            if(title.isEmpty() ){ //|| dimension1.isEmpty() || dimension2.isEmpty() || dimension3.isEmpty()
                return false;
            }
        }

        return true;
    }

}
