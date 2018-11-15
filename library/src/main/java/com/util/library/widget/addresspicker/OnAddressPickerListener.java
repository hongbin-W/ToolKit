package com.util.library.widget.addresspicker;

import com.util.library.widget.addresspicker.bean.City;
import com.util.library.widget.addresspicker.bean.County;
import com.util.library.widget.addresspicker.bean.Province;
import com.util.library.widget.addresspicker.bean.Street;

/**
 * @description: 地址选择后回调接口
 * @author: whb
 * @date: 2018/11/14 17:35
 */
public interface OnAddressPickerListener {
    void onAddressPicked(Province province, City city, County county, Street street);
}
