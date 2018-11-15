package com.util.toolkit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.util.library.widget.addresspicker.AddressPicker;
import com.util.library.widget.addresspicker.BottomDialog;
import com.util.library.widget.addresspicker.OnAddressPickerListener;
import com.util.library.widget.addresspicker.bean.City;
import com.util.library.widget.addresspicker.bean.County;
import com.util.library.widget.addresspicker.bean.Province;
import com.util.library.widget.addresspicker.bean.Street;


public class MainActivity extends AppCompatActivity  implements View.OnClickListener, OnAddressPickerListener, AddressPicker.OnDialogCloseListener {


    private LinearLayout mViewcontent;
    private TextView mSelectorContent;
    private String provinceCode;
    private String cityCode;
    private String countyCode;
    private String streetCode;
    String url = "http://www.365aq.cn/api/public/region/tree?parentid=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSelectorContent = (TextView) findViewById(R.id.tv_selector_area);
        mViewcontent = (LinearLayout) findViewById(R.id.content);
        mSelectorContent.setOnClickListener(this);
    }

    private BottomDialog dialog;

    @Override
    public void onClick(View v) {
        if (dialog != null) {
            dialog.show();
        } else {
            dialog = new BottomDialog(this,url,-1);
            //地址选取监听
            dialog.setOnAddressPickerListener(this);
            //Dialog监听
            dialog.setDialogDismisListener(this);
            //设置字体的大小
            dialog.setTextSize(14);
            //设置指示器的颜色
            dialog.setIndicatorBackgroundColor(R.color.holo_red_dark);
            //设置字体获得焦点的颜色
            dialog.setTextSelectedColor(android.R.color.holo_red_dark);
            //设置字体没有获得焦点的颜色
            dialog.setTextUnSelectedColor(android.R.color.holo_blue_light);
            dialog.show();
        }
    }


    @Override
    public void onAddressPicked(Province province, City city, County county, Street street) {
        provinceCode = (province == null ? "" : province.getName());
        cityCode = (city == null ? "" : city.getName());
        countyCode = (county == null ? "" : county.getName());
        streetCode = (street == null ? "" : street.getName());

        String address = (province == null ? "" : province.getName()) + " " + (city == null ? "" : city.getName()) + " " + (county == null ? "" : county.getName()) + " " + (street == null ? "" : street.getName());

        mSelectorContent.setText("地址 :" + " " + address);

        if (dialog != null) {
            dialog.dismiss();
        }
//        getSelectedArea();
    }

    @Override
    public void dialogclose() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
