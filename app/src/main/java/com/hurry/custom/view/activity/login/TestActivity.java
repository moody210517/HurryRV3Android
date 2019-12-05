package com.hurry.custom.view.activity.login;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.aigestudio.wheelpicker.WheelPicker;
import com.aigestudio.wheelpicker.widgets.WheelDatePicker;
import com.hurry.custom.R;
import com.hurry.custom.common.utils.DeviceUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 3/18/2017.
 */

public class TestActivity extends Activity implements WheelPicker.OnItemSelectedListener, View.OnClickListener{


    private WheelDatePicker wheelDatePicker;
    private WheelPicker wheelHour;
    private WheelPicker wheelMinute;
    private WheelPicker wheelAp;


    int selectedYear, selectedMonth, selectedDay;
    int selectedHour, selectedMinute;
    String status = "AM";

    ArrayList<String> hours = new ArrayList<String>();
    ArrayList<String> minutes = new ArrayList<String>();
    ArrayList<String> aps = new ArrayList<String>();
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_test);
        DeviceUtil.hideStatusbar(this);

        wheelDatePicker = (WheelDatePicker) findViewById(R.id.wheel_date_picker);
        Calendar calendar = Calendar.getInstance();
        wheelDatePicker.setSelectedYear(calendar.get(Calendar.YEAR));
        wheelDatePicker.setSelectedMonth(calendar.get(Calendar.MONTH));
        wheelDatePicker.setSelectedDay(calendar.get(Calendar.DATE));
        wheelDatePicker.setOnDateSelectedListener(new WheelDatePicker.OnDateSelectedListener() {
            @Override
            public void onDateSelected(WheelDatePicker picker, Date date) {
                selectedMonth = picker.getSelectedMonth();
                selectedYear = picker.getSelectedYear();
                selectedDay = picker.getSelectedDay();
            }
        });
        wheelDatePicker.setSelectedItemTextColor(getColor(R.color.red_pop));
        wheelDatePicker.setItemTextColor(getColor(R.color.wheel_text_color));
        wheelDatePicker.setCurved(true);
        wheelDatePicker.setItemTextSize(42);

        wheelHour = (WheelPicker) findViewById(R.id.wheel_hour);

        for(int k = 1 ; k <= 12 ; k++){
            hours.add(String.valueOf(k));
        }
        wheelHour.setData(hours);
        wheelHour.setOnItemSelectedListener(this);
        wheelHour.setItemTextSize(42);

        wheelMinute= (WheelPicker) findViewById(R.id.wheel_minute);

        for(int k = 1 ; k <= 60 ; k++){
            minutes.add(String.valueOf(k));
        }
        wheelMinute.setOnItemSelectedListener(this);
        wheelMinute.setData(minutes);
        wheelMinute.setItemTextSize(42);

        wheelAp= (WheelPicker) findViewById(R.id.wheel_ap);

        aps.add("AM");
        aps.add("PM");
        wheelAp.setOnItemSelectedListener(this);
        wheelAp.setData(aps);
        wheelAp.setItemTextSize(42);
    }


    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {
        switch (picker.getId()){
            case R.id.wheel_hour:
                selectedHour = Integer.valueOf(hours.get(wheelHour.getCurrentItemPosition()));
                break;
            case R.id.wheel_minute:
                selectedMinute = Integer.valueOf(minutes.get(wheelMinute.getCurrentItemPosition()));
                break;
            case R.id.wheel_ap:
                status = aps.get(wheelAp.getCurrentItemPosition());
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }
}
