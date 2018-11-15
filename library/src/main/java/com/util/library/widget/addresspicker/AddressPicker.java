package com.util.library.widget.addresspicker;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.util.library.R;
import com.util.library.widget.addresspicker.bean.City;
import com.util.library.widget.addresspicker.bean.County;
import com.util.library.widget.addresspicker.bean.Entity;
import com.util.library.widget.addresspicker.bean.Province;
import com.util.library.widget.addresspicker.bean.Street;
import com.util.library.widget.addresspicker.tool.ListUtil;

import java.util.List;

/**
 * @description:
 * @author: whb
 * @date: 2018/11/13 15:56
 */
public class AddressPicker implements AdapterView.OnItemClickListener {

    private Context context;
    private final LayoutInflater inflater;
    private View view;

    /**
     * 下标指示器视图
     */
    private View indicator;
    /**
     * 地址显示
     */
    private LinearLayout ll_tab;
    private TextView tv_province;
    private TextView tv_city;
    private TextView tv_county;
    private TextView tv_street;
    /**
     * 关闭图标
     */
    private ImageView iv_close;
    /**
     * 加载条
     */
    private ProgressBar progressBar;
    /**
     * 地址列表
     */
    private ListView listView;


    /**
     * 默认标识
     */
    private int tabIndex = PROVINCE_FLAG;
    /**
     * 省份标识
     */
    private static final int PROVINCE_FLAG = 0;
    /**
     * 城市标识
     */
    private static final int CITY_FLAG = 1;
    /**
     * 乡镇标识
     */
    private static final int COUNTY_FLAG = 2;
    /**
     * 街道标识
     */
    private static final int STREET_FLAG = 3;

    /**
     * 下标初始值
     */
    private static final int INDEX_INIT = -1;
    /**
     * 省份下标
     */
    private int provinceIndex = INDEX_INIT;
    /**
     * 城市下标
     */
    private int cityIndex = INDEX_INIT;
    /**
     * 乡镇下标
     */
    private int countyIndex = INDEX_INIT;
    /**
     * 街道下标
     */
    private int streetIndex = INDEX_INIT;

    /**
     * 存储地址列表
     */
    private List<Province> list_province;
    private List<City> list_city;
    private List<County> list_country;
    private List<Street> list_street;
    /**
     * 选中和未选中颜色值
     */
    private int pickedColor;
    private int unPickedColor;

    private ProvinceAdapter provinceAdapter;
    private CityAdapter cityAdapter;
    private CountyAdapter countyAdapter;
    private StreetAdapter streetAdapter;

    private NetWorkManager netWorkManager;

    /**
     * 地址选择后接口
     */
    private OnAddressPickerListener listener;
    private OnDialogCloseListener dialogCloseListener;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PROVINCE_FLAG:
                    //省份数据列表更新
                    list_province = (List<Province>) msg.obj;
                    provinceAdapter.notifyDataSetChanged();
                    listView.setAdapter(provinceAdapter);
                    break;

                case CITY_FLAG:
                    list_city = (List<City>) msg.obj;
                    cityAdapter.notifyDataSetChanged();
                    if (ListUtil.notEmpty(list_city)) {
                        //刷新城市列表
                        listView.setAdapter(cityAdapter);
                        //更新下标
                        tabIndex = CITY_FLAG;
                    } else {
                        callBackMessage();
                    }
                    break;

                case COUNTY_FLAG:
                    list_country = (List<County>) msg.obj;
                    countyAdapter.notifyDataSetChanged();
                    if (ListUtil.notEmpty(list_country)) {
                        //刷新区县列表
                        listView.setAdapter(countyAdapter);
                        //更新下标
                        tabIndex = COUNTY_FLAG;
                    } else {
                        callBackMessage();
                    }
                    break;

                case STREET_FLAG:
                    list_street = (List<Street>) msg.obj;
                    streetAdapter.notifyDataSetChanged();
                    if (ListUtil.notEmpty(list_street)) {
                        //刷新街道列表
                        listView.setAdapter(streetAdapter);
                        //更新下标
                        tabIndex = STREET_FLAG;
                    } else {
                        callBackMessage();
                    }
                    break;

                default:
                        break;
            }
            //刷新tab
            refreshTabVisibity();
            //刷新进度条
            refreshProgressVisibility();
            //刷新指示器
            refreshIndicator();

            return true;
        }
    });


    public AddressPicker(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        netWorkManager = new NetWorkManager(context);
        initView();
        initAdapter();
        getProvinceList();
    }

    /**
     * 初始化布局视图
     * @author: whb
     * @date: 2018/11/14 10:37
     **/
    private void initView() {
        view = inflater.inflate(R.layout.address_picker, null);
        //指示器
        this.indicator = view.findViewById(R.id.indicator);
        this.ll_tab = (LinearLayout) view.findViewById(R.id.ll_tab);
        //省份
        this.tv_province = (TextView) view.findViewById(R.id.tv_province);
        //城市
        this.tv_city = (TextView) view.findViewById(R.id.tv_city);
        //区 乡镇
        this.tv_county = (TextView) view.findViewById(R.id.tv_county);
        //街道
        this.tv_street = (TextView) view.findViewById(R.id.tv_street);
        //进度条
        this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        this.iv_close = (ImageView) view.findViewById(R.id.iv_colse);
        this.listView = (ListView) view.findViewById(R.id.listView);

        //地址列表监听
        this.listView.setOnItemClickListener(this);
        //省份事件监听
        this.tv_province.setOnClickListener(new OnProvinceTabClickListener());
        //城市事件监听
        this.tv_city.setOnClickListener(new OnCityTabClickListener());
        //区 乡镇事件监听
        this.tv_county.setOnClickListener(new OnCountyTabClickListener());
        //街道事件监听
        this.tv_street.setOnClickListener(new OnStreetTabClickListener());
        this.iv_close.setOnClickListener(new onCloseClickListener());

        refreshIndicator();
    }

    /**
     * 初始化所有适配器
     * @author: whb
     * @date: 2018/11/14 14:09
     **/
    private void initAdapter() {
        provinceAdapter = new ProvinceAdapter();
        cityAdapter = new CityAdapter();
        countyAdapter = new CountyAdapter();
        streetAdapter = new StreetAdapter();
    }

    /**
     * 省份点击事件监听
     * @author: whb
     * @date: 2018/11/14 11:01
     **/
    class OnProvinceTabClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            tabIndex = PROVINCE_FLAG;
            listView.setAdapter(provinceAdapter);
            //选择省份下标
            if (INDEX_INIT != provinceIndex) {
                listView.setSelection(provinceIndex);
            }

            refreshTabVisibity();
            refreshIndicator();
        }
    }
    
    /**
     * 城市点击事件监听
     * @author: whb
     * @date: 2018/11/14 11:08
     **/
    class OnCityTabClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            tabIndex = CITY_FLAG;
            listView.setAdapter(cityAdapter);
            //选择城市下标
            if (INDEX_INIT != cityIndex) {
                listView.setSelection(cityIndex);
            }
            refreshTabVisibity();
            refreshIndicator();
        }
    }

    /**
     * 区 镇点击事件监听
     * @author: whb
     * @date: 2018/11/14 11:15
     **/
    class OnCountyTabClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            tabIndex = COUNTY_FLAG;
            listView.setAdapter(countyAdapter);
            //选择区 镇下标
            if (INDEX_INIT != countyIndex) {
                listView.setSelection(countyIndex);
            }
            refreshTabVisibity();
            refreshIndicator();
        }
    }

    /**
     * 街道点击事件监听
     * @author: whb
     * @date: 2018/11/14 11:14
     **/
    class OnStreetTabClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            tabIndex = STREET_FLAG;
            listView.setAdapter(streetAdapter);
            if (INDEX_INIT != streetIndex) {
                listView.setSelection(streetIndex);
            }
            refreshTabVisibity();
            refreshIndicator();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (tabIndex) {
            case PROVINCE_FLAG: {
                Province province = provinceAdapter.getItem(position);

                // 更新当前级别及子级标签文本
                tv_province.setText(province.getName());
                tv_city.setText("请选择");
                tv_county.setText("请选择");
                tv_street.setText("请选择");
                //根据省份的id,从数据库中查询城市列表
                getCityList(province.getId());

                // 清空子级数据
                list_city = null;
                list_country = null;
                list_street = null;
                cityAdapter.notifyDataSetChanged();
                countyAdapter.notifyDataSetChanged();
                streetAdapter.notifyDataSetChanged();
                // 更新已选中项
                this.provinceIndex = position;
                this.cityIndex = INDEX_INIT;
                this.countyIndex = INDEX_INIT;
                this.streetIndex = INDEX_INIT;
                // 更新选中效果
                provinceAdapter.notifyDataSetChanged();
                break;
            }
            case CITY_FLAG:
                //城市
                City city = cityAdapter.getItem(position);
                tv_city.setText(city.getName());
                tv_county.setText("请选择");
                tv_street.setText("请选择");
                //根据城市的id,从数据库中查询城市列表
                getCountyList(city.getId());
                // 清空子级数据
                list_country = null;
                list_street = null;
                countyAdapter.notifyDataSetChanged();
                streetAdapter.notifyDataSetChanged();
                // 更新已选中项
                this.cityIndex = position;
                this.countyIndex = INDEX_INIT;
                this.streetIndex = INDEX_INIT;
                // 更新选中效果
                cityAdapter.notifyDataSetChanged();
                break;
            case COUNTY_FLAG:
                County county = countyAdapter.getItem(position);

                tv_county.setText(county.getName());
                tv_street.setText("请选择");
                getStreetList(county.getId());

                list_street = null;
                streetAdapter.notifyDataSetChanged();

                this.countyIndex = position;
                this.streetIndex = INDEX_INIT;

                countyAdapter.notifyDataSetChanged();
                break;
            case STREET_FLAG:
                Street street = streetAdapter.getItem(position);
                tv_street.setText(street.getName());

                this.streetIndex = position;

                streetAdapter.notifyDataSetChanged();

                callBackMessage();
                break;
            default:
                Province province = provinceAdapter.getItem(position);

                // 更新当前级别及子级标签文本
                tv_province.setText(province.getName());
                tv_city.setText("请选择");
                tv_county.setText("请选择");
                tv_street.setText("请选择");
                //根据省份的id,从数据库中查询城市列表
                getProvinceList();

                // 清空子级数据
                list_city = null;
                list_country = null;
                list_street = null;
                cityAdapter.notifyDataSetChanged();
                countyAdapter.notifyDataSetChanged();
                streetAdapter.notifyDataSetChanged();
                // 更新已选中项
                this.provinceIndex = position;
                this.cityIndex = INDEX_INIT;
                this.countyIndex = INDEX_INIT;
                this.streetIndex = INDEX_INIT;
                // 更新选中效果
                provinceAdapter.notifyDataSetChanged();
                break;
        }
    }

    /**
     * 网络请求获取省份列表
     * @author: whb
     * @date: 2018/11/14 14:26
     **/
    private void getProvinceList() {
        progressBar.setVisibility(View.VISIBLE);
        netWorkManager.getRegionData(-1,PROVINCE_FLAG ,new NetWorkManager.OnNetworkAccessToListListener<Entity>() {
            @Override
            public void onSuccess(List<Entity> list) {
                handler.sendMessage(Message.obtain(handler, PROVINCE_FLAG, list));
            }

            @Override
            public void onFail(String message) {

            }
        });
    }

    /**
     * 网络请求获取城市列表
     * @author: whb
     * @date: 2018/11/14 19:26
     **/
    private void getCityList(long id) {
        progressBar.setVisibility(View.VISIBLE);
        netWorkManager.getRegionData(id, CITY_FLAG, new NetWorkManager.OnNetworkAccessToListListener<Entity>() {
            @Override
            public void onSuccess(List<Entity> list) {
                handler.sendMessage(Message.obtain(handler, CITY_FLAG, list));
            }

            @Override
            public void onFail(String message) {

            }
        });
    }

    /**
     * 网络请求获取城市列表
     * @author: whb
     * @date: 2018/11/14 19:26
     **/
    private void getCountyList(long id) {
        progressBar.setVisibility(View.VISIBLE);
        netWorkManager.getRegionData(id, COUNTY_FLAG, new NetWorkManager.OnNetworkAccessToListListener<Entity>() {
            @Override
            public void onSuccess(List<Entity> list) {
                handler.sendMessage(Message.obtain(handler, COUNTY_FLAG, list));
            }

            @Override
            public void onFail(String message) {

            }
        });
    }

    /**
     * 网络请求获取城市列表
     * @author: whb
     * @date: 2018/11/14 19:26
     **/
    private void getStreetList(long id) {
        progressBar.setVisibility(View.VISIBLE);
        netWorkManager.getRegionData(id, STREET_FLAG, new NetWorkManager.OnNetworkAccessToListListener<Entity>() {
            @Override
            public void onSuccess(List<Entity> list) {
                handler.sendMessage(Message.obtain(handler, STREET_FLAG, list));
            }

            @Override
            public void onFail(String message) {

            }
        });
    }

    /**
     * 各级区域选择完成以后 进行回调
     * @author: whb
     * @date: 2018/11/14 18:47
     **/
    private void callBackMessage(){
        if (null != listener) {
            Province province = null == list_province || provinceIndex == INDEX_INIT ? null : list_province.get(provinceIndex);
            City city = null == list_city || cityIndex == INDEX_INIT ? null : list_city.get(cityIndex);
            County county = null == list_country || countyIndex == INDEX_INIT ? null : list_country.get(countyIndex);
            Street street = null == list_street || streetIndex == INDEX_INIT ? null : list_street.get(streetIndex);

            listener.onAddressPicked(province,city,county,street);
        }
    }

    /**
     * 刷新tab页面数据显示
     * @author: whb
     * @date: 2018/11/14 19:07
     **/
    private void refreshTabVisibity() {
        tv_province.setVisibility(ListUtil.notEmpty(list_province) ? View.VISIBLE:View.GONE);
        tv_city.setVisibility(ListUtil.notEmpty(list_city) ? View.VISIBLE:View.GONE);
        tv_county.setVisibility(ListUtil.notEmpty(list_country) ? View.VISIBLE:View.GONE);
        tv_street.setVisibility(ListUtil.notEmpty(list_street) ? View.VISIBLE:View.GONE);
        //设置按钮能否点击 false 不能点击 true 能点击
        tv_province.setEnabled(tabIndex != PROVINCE_FLAG);
        tv_city.setEnabled(tabIndex != CITY_FLAG);
        tv_county.setEnabled(tabIndex != COUNTY_FLAG);
        tv_street.setEnabled(tabIndex != STREET_FLAG);
        if(pickedColor != 0 && unPickedColor != 0){
            refreshTabTextColor();
        }
    }

    /**
     * 刷新tab字体颜色
     * @author: whb
     * @date: 2018/11/14 19:07
     **/
    private void refreshTabTextColor() {
        if (tabIndex != PROVINCE_FLAG) {
            tv_province.setTextColor(context.getResources().getColor(pickedColor));
        } else {
            tv_province.setTextColor(context.getResources().getColor(unPickedColor));
        }
        if (tabIndex != CITY_FLAG) {
            tv_city.setTextColor(context.getResources().getColor(pickedColor));
        } else {
            tv_city.setTextColor(context.getResources().getColor(unPickedColor));
        }
        if (tabIndex != COUNTY_FLAG) {
            tv_county.setTextColor(context.getResources().getColor(pickedColor));
        } else {
            tv_county.setTextColor(context.getResources().getColor(unPickedColor));
        }
        if (tabIndex != STREET_FLAG) {
            tv_street.setTextColor(context.getResources().getColor(pickedColor));
        } else {
            tv_street.setTextColor(context.getResources().getColor(unPickedColor));
        }
    }

    /**
     * 刷新文字下面指示器
     * @author: whb
     * @date: 2018/11/14 19:46
     **/
    private void refreshIndicator() {
        view.post(new Runnable() {
            @Override
            public void run() {
                switch (tabIndex) {
                    case PROVINCE_FLAG:
                        //省份
                        indicatorAnimator(tv_province).start();
                        break;
                    case CITY_FLAG:
                        //城市
                        indicatorAnimator(tv_city).start();
                        break;
                    case COUNTY_FLAG:
                        //乡镇
                        indicatorAnimator(tv_county).start();
                        break;
                    case STREET_FLAG:
                        //街道
                        indicatorAnimator(tv_street).start();
                        break;
                    default:
                        //省份
                        indicatorAnimator(tv_province).start();
                        break;
                }
            }
        });
    }

    /**
     * 构建指示器动画效果
     * @author: whb
     * @date: 2018/11/14 19:53
     **/
    private AnimatorSet indicatorAnimator(TextView tab) {
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(indicator, "X", indicator.getX(), tab.getX());

        final ViewGroup.LayoutParams params = indicator.getLayoutParams();
        ValueAnimator widthAnimator = ValueAnimator.ofInt(params.width, tab.getMeasuredWidth());
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.width = (int) animation.getAnimatedValue();
                indicator.setLayoutParams(params);
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.playTogether(xAnimator, widthAnimator);

        return set;
    }

    /**
     *设置字体选中的颜色
     */
    public void setTextSelectedColor(int pickedColor){
        this.pickedColor = pickedColor;
    }

    /**
     *设置字体没有选中的颜色
     */
    public void setTextUnSelectedColor(int unPickedColor){
        this.unPickedColor = unPickedColor;
    }
    /**
     * 设置字体的大小
     */
    public void setTextSize(float dp){
        tv_province.setTextSize(dp);
        tv_city.setTextSize(dp);
        tv_county.setTextSize(dp);
        tv_street.setTextSize(dp);
    }

    /**
     * 设置字体的背景
     */
    public void setBackgroundColor(int colorId){
        ll_tab.setBackgroundColor(context.getResources().getColor(colorId));
    }

    /**
     * 设置指示器的背景
     */
    public void setIndicatorBackgroundColor(int colorId){
        indicator.setBackgroundColor(context.getResources().getColor(colorId));
    }
    /**
     * 设置指示器的背景
     */
    public void setIndicatorBackgroundColor(String color){
        indicator.setBackgroundColor(Color.parseColor(color));
    }

    /**
     * 控制进度条显示与否
     * @author: whb
     * @date: 2018/11/14 19:56
     **/
    private void refreshProgressVisibility() {
        ListAdapter adapter = listView.getAdapter();
        int itemCount = adapter.getCount();
        progressBar.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
    }

    /**
     * 获得view
     * @return
     */
    public View getView() {
        return view;
    }

    /**
     * 省份列表的适配器
     * @author: whb
     * @date: 2018/11/14 11:26
     **/
    class ProvinceAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return null == list_province ? 0:list_province.size();
        }

        @Override
        public Province getItem(int position) {
            return list_province.get(position);
        }

        @Override
        public long getItemId(int position) {
            return list_province.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);
                viewHolder.iv_checked = (ImageView) convertView.findViewById(R.id.iv_checked);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //显示省份名字
            Province item = getItem(position);
            viewHolder.textView.setText(item.getName());

            boolean checked = provinceIndex != INDEX_INIT && list_province.get(provinceIndex).getId().equals(item.getId());
            viewHolder.textView.setEnabled(!checked);
            viewHolder.iv_checked.setVisibility(checked ? View.VISIBLE : View.GONE);

            return convertView;
        }

        class ViewHolder{
            TextView textView;
            ImageView iv_checked;
        }
    }

    /**
     * 城市列表的适配器
     * @author: whb
     * @date: 2018/11/14 11:27
     **/
    class CityAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return null == list_city ? 0:list_city.size();
        }

        @Override
        public City getItem(int position) {
            return list_city.get(position);
        }

        @Override
        public long getItemId(int position) {
            return list_city.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);
                viewHolder.iv_checked = (ImageView) convertView.findViewById(R.id.iv_checked);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            City city = getItem(position);
            viewHolder.textView.setText(city.getName());

            boolean checked = cityIndex != INDEX_INIT && list_city.get(cityIndex).getId().equals(city.getId());
            viewHolder.textView.setEnabled(!checked);
            viewHolder.iv_checked.setVisibility(checked ? View.VISIBLE : View.GONE);

            return convertView;
        }

        class ViewHolder{
            TextView textView;
            ImageView iv_checked;
        }
    }

    /**
     * 区镇列表的适配器
     * @author: whb
     * @date: 2018/11/14 11:27
     **/
    class CountyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return null == list_country ? 0 : list_country.size();
        }

        @Override
        public County getItem(int position) {
            return list_country.get(position);
        }

        @Override
        public long getItemId(int position) {
            return list_country.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);
                viewHolder.iv_checked = (ImageView) convertView.findViewById(R.id.iv_checked);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            County country = getItem(position);
            viewHolder.textView.setText(country.getName());

            boolean checked = countyIndex != INDEX_INIT && list_country.get(countyIndex).getId().equals(country.getId());
            viewHolder.textView.setEnabled(!checked);
            viewHolder.iv_checked.setVisibility(checked ? View.VISIBLE : View.GONE);

            return convertView;
        }

        class ViewHolder{
            TextView textView;
            ImageView iv_checked;
        }
    }

    /**
     * 街道列表的适配器
     * @author: whb
     * @date: 2018/11/14 11:27
     **/
    class StreetAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return null == list_street ? 0 : list_street.size();
        }

        @Override
        public Street getItem(int position) {
            return list_street.get(position);
        }

        @Override
        public long getItemId(int position) {
            return list_street.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);
                viewHolder.iv_checked = (ImageView) convertView.findViewById(R.id.iv_checked);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Street street = getItem(position);
            viewHolder.textView.setText(street.getName());

            boolean checked = streetIndex != INDEX_INIT && list_street.get(streetIndex).getId().equals(street.getId());
            viewHolder.textView.setEnabled(!checked);
            viewHolder.iv_checked.setVisibility(checked ? View.VISIBLE : View.GONE);

            return convertView;
        }

        class ViewHolder{
            TextView textView;
            ImageView iv_checked;
        }
    }


    public void setOnAddressPickerListener(OnAddressPickerListener listener) {
        this.listener = listener;
    }

    public interface OnDialogCloseListener{
        void dialogclose();
    }
    /**
     * 设置close监听
     */
    public void setOnDialogCloseListener(OnDialogCloseListener listener) {
        this.dialogCloseListener = listener;
    }

    /**
     * 点击右边关闭dialog监听
     */
    class onCloseClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(dialogCloseListener!=null){
                dialogCloseListener.dialogclose();
            }
        }
    }
}
