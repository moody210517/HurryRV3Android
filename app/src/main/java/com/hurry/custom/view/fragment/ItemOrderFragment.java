/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hurry.custom.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.view.activity.AddressDetailsNewActivity;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.adapter.CustomerAdapter;
import org.angmarch.views.NiceSpinner;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemOrderFragment extends Fragment implements View.OnClickListener{

    Context mContext;

    @BindView(R.id.lin_item) LinearLayout linItem;
    @BindView(R.id.txt_add_more) TextView txtMore;
    @BindView(R.id.btn_continue) Button btnContinue;
    @BindView(R.id.scrollview) ScrollView scrollView;
    @BindView(R.id.rl_station) RelativeLayout rlStation;
    @BindView(R.id.rl_city) RelativeLayout rlCity;
    @BindView(R.id.rl_international) RelativeLayout rlInternational;
    @BindView(R.id.txt_header) TextView txtHeader;


    String type = "";
    public ItemOrderFragment(String type){
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = (View) inflater.inflate(
                R.layout.fragment_item_order, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        Constants.initWeight();

        if(Constants.itemOrderModel.itemModels.size() == 0){
            Constants.itemOrderModel.itemModels.clear();
            ItemModel itemModel = new ItemModel();
            itemModel.title = "";
            itemModel.dimension1 = "";
            itemModel.dimension2 = "";
            itemModel.dimension3 = "";
            itemModel.package_cost = "";
            itemModel.weight = Constants.weight.get(0);
            itemModel.weight_value = Constants.weight_value.get(0);
            itemModel.quantity = Constants.quantity.get(0);
            Constants.itemOrderModel.itemModels.add(itemModel);
        }

        updateView();
        return view;
    }


    @Override
    public void onResume(){
        initView();
        super.onResume();
    }


    private void initView(){
        txtMore.setOnClickListener(this);
        btnContinue.setOnClickListener(this);

        if(Constants.DELIVERY_STATUS == Constants.SAME_CITY){
            rlCity.setBackgroundResource(R.drawable.ic_triangle_left_teal);
            txtHeader.setText(getString(R.string.same_city));

        }else if(Constants.DELIVERY_STATUS == Constants.OUT_STATION){
            rlStation.setBackgroundResource(R.drawable.ic_triangle_right_teal);
            txtHeader.setText(getString(R.string.out_station));
        }else if(Constants.DELIVERY_STATUS == Constants.INTERNATIONAL){
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
        LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            //String [] itemLists = new String[Constants.itemLists.size()];
            ArrayList<String> itemArray = new ArrayList<>();
            if(Constants.DELIVERY_STATUS == Constants.INTERNATIONAL){
                itemArray.add("Food - Sweets");
                itemArray.add("Food - Snacks");
                itemArray.add("Food - Pickles");
                itemArray.add("Documents");
                itemArray.add("Clothes");
                itemArray.add("Medicines");
            }else{
                for(int j = 0 ; j < Constants.itemLists.size(); j++){
              //      itemLists[j] = Constants.itemLists.get(j).title;
                    itemArray.add(Constants.itemLists.get(j).title);
                }
            }


            CustomerAdapter cityAdapter = new CustomerAdapter(mContext , R.layout.custom_auto, itemArray);

            edtItem.setAdapter(cityAdapter);
            edtItem.setThreshold(1);//will start working from first character

            EditText edtPackageCost = (EditText)v.findViewById(R.id.edt_cost);
            final EditText edtDimension1 = (EditText)v.findViewById(R.id.edt_dimension1);
            final EditText edtDimension2 = (EditText)v.findViewById(R.id.edt_dimension2);
            final EditText edtDimension3 = (EditText)v.findViewById(R.id.edt_dimension3);

            edtItem.setText(itemModel.title);

            edtPackageCost.setText(itemModel.package_cost);
            edtPackageCost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Constants.itemOrderModel.itemModels.get(finalK1).package_cost = edtPackageCost.getText().toString();
                }
            });


            edtDimension1.setText(itemModel.dimension1);
            edtDimension2.setText(itemModel.dimension2);
            edtDimension3.setText(itemModel.dimension3);


            final NiceSpinner spQuantity = (NiceSpinner)v.findViewById(R.id.sp_quantity);
            //spQuantity.type = 1;
            spQuantity.setPadding(20, 10,10,10);
            spQuantity.setGravity(Gravity.CENTER);

            //spQuantity.setBackgroundColor(getResources().getColor(R.color.light_mustard_color));
            final NiceSpinner spWeight = (NiceSpinner)v.findViewById(R.id.sp_weight);
            //spWeight.type = 1;
            spWeight.setPadding(20, 10,10,10);
            spWeight.setGravity(Gravity.CENTER);
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
                    Toast.makeText(mContext, "Can not upload more", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_continue:
                if(checkInput()){
                    ((HomeActivity)mContext).updateFragment(HomeActivity.ADDRESS_DETAILS, "");
//                    Intent intent = new Intent(mContext, AddressDetailsNewActivity.class);
//                    startActivity(intent);
                }else{
                    Toast.makeText(mContext, getResources().getString(R.string.input_all), Toast.LENGTH_SHORT).show();
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
