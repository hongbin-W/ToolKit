package com.util.library.widget.addresspicker;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.util.library.widget.addresspicker.bean.City;
import com.util.library.widget.addresspicker.bean.County;
import com.util.library.widget.addresspicker.bean.Entity;
import com.util.library.widget.addresspicker.bean.Province;
import com.util.library.widget.addresspicker.bean.Street;

import org.json.JSONArray;

import java.util.List;

/**
 * @description: 网络请求数据
 * @author: whb
 * @date: 2018/11/14 14:27
 */
public class NetWorkManager {

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

    private Gson gson;
    private Context context;

    private String url = "http://www.365aq.cn/api/public/region/tree?parentid=";

    RequestQueue queue;

    public NetWorkManager(Context context) {
        this.context = context;
        queue = Volley.newRequestQueue(context);
        gson = new Gson();
    }

    /**
     * 获取省份列表
     * @author: whb
     * @date: 2018/11/14 14:59
     **/
    public void getRegionData(long id, final int flag, final OnNetworkAccessToListListener<Entity> listener) {
        url = url + id;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                url = "http://www.365aq.cn/api/public/region/tree?parentid=";
                List<Entity> list = null;
                if (PROVINCE_FLAG == flag) {
                    list = gson.fromJson(response.toString(), new TypeToken<List<Province>>() {
                    }.getType());
                } else if (CITY_FLAG == flag) {
                    list = gson.fromJson(response.toString(), new TypeToken<List<City>>() {
                    }.getType());
                } else if (COUNTY_FLAG == flag) {
                    list = gson.fromJson(response.toString(), new TypeToken<List<County>>() {
                    }.getType());
                } else if (STREET_FLAG == flag) {
                    list = gson.fromJson(response.toString(), new TypeToken<List<Street>>() {
                    }.getType());
                }
                listener.onSuccess(list);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFail("数据获取失败");
            }
        });
        queue.add(request);
    }

    /**
     * 返回数据接口
     * @author: whb
     * @date: 2018/11/14 15:19
     **/
    public interface OnNetworkAccessToListListener<T> {

        void onSuccess(List<T> list);

        void onFail(String message);
    }
}
