package com.util.library.widget.addresspicker;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.util.library.R;


/**
 * Created by smartTop on 2016/10/19.
 */

public class BottomDialog extends Dialog {

    private AddressPicker picker;
    /**
     * 初始化Dialog
     * @author: whb
     * @date: 2018/11/15 13:45
     * @param context 上下文
     * @param url 请求网络地址
     * @param id 首层数据参数
     **/
    public BottomDialog(Context context, String url, long id) {
        super(context, R.style.bottom_dialog);
        init(context,url,id);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context,String url,long id) {
        picker = new AddressPicker(context,url,id);
        setContentView(picker.getView());

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = (int) Math.ceil((double)(context.getResources().getDisplayMetrics().density * 360));
        window.setAttributes(params);

        window.setGravity(Gravity.BOTTOM);
    }

    public void setOnAddressPickerListener(OnAddressPickerListener listener) {
        this.picker.setOnAddressPickerListener(listener);
    }

    public void setDialogDismisListener(AddressPicker.OnDialogCloseListener listener){
        this.picker.setOnDialogCloseListener(listener);
    }
    /**
     *设置字体选中的颜色
     */
    public void setTextSelectedColor(int selectedColor){
        this.picker.setTextPickedColor(selectedColor);
    }
    /**
     *设置字体没有选中的颜色
     */
    public void setTextUnSelectedColor(int unSelectedColor){
        this.picker.setTextUnPickedColor(unSelectedColor);
    }
    /**
     * 设置字体的大小
     */
    public void setTextSize(float dp){
       this.picker.setTextSize(dp);
    }
    /**
     * 设置字体的背景
     */
    public void setBackgroundColor(int colorId){
       this.picker.setBackgroundColor(colorId);
    }
    /**
     * 设置指示器的背景
     */
    public void setIndicatorBackgroundColor(int colorId){
        this.picker.setIndicatorBackgroundColor(colorId);
    }
    /**
     * 设置指示器的背景
     */
    public void setIndicatorBackgroundColor(String color){
        this.picker.setIndicatorBackgroundColor(color);
    }
}
