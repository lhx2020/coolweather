package com.coolweather.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    private List<Province> provincesList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectProvince;
    private City selectCity;
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("aaaaa:", "currentLevel="+currentLevel+" position="+position);
                if (currentLevel == LEVEL_PROVINCE) {
                    selectProvince = provincesList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectCity = cityList.get(position);
                   queryCounties();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();

    }

    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provincesList = LitePal.findAll(Province.class);
        if (provincesList.size() > 1) {
            dataList.clear();
            for (Province province : provincesList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
             String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");

        }
    }

    private void queryCities() {
        Log.d("aaaaa",selectProvince.getProvinceName()+"   queryCities");
        titleText.setText(selectProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceid = ? ", String.valueOf(selectProvince.getId())).find(City.class);
        if (cityList.size() > 1) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            Log.d("aaaaa",selectProvince.getProvinceName()+"   "+address);
            queryFromServer(address, "city");
        }
    }

    //查询县
    private void queryCounties() {
        titleText.setText(selectCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = LitePal.where("cityid = ?", String.valueOf(selectCity.getId())).find(County.class);

        if (countyList.size() > 1) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }

    }
    public void getjson(){
        //TODO 1:client对象
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
       builder.callTimeout(5, TimeUnit.SECONDS);//连接超时
        builder.readTimeout(5,TimeUnit.SECONDS);//读取超时
        OkHttpClient client = builder.build();
        //TODO 2：request对象
        Request.Builder builder1 = new Request.Builder();
        builder1.url("http://guolin.tech/api/china");
       //builder1.url("http://www.baidu.com");//设置网址
        builder1.get();//设置请求方法
        Request request = builder1.build();
        //TODO 3:发起连接call
        Call call = client.newCall(request);
        //TODO 4:通过call得到response
        call.enqueue(new Callback() {
            //请求失败
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("requestrrrrrrrrrrrrrrrrrrrrrrrrrr","失败！");
            }
            //请求成功
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获得响应体：json串
                ResponseBody body = response.body();
                //通过body直接转成字符串
                String json = body.string();
                Log.d("requestrrrrrrrrrrrrrrrrrrrrrrrrrr",json);
                // Toast.makeText(MainActivity.this, ""+json, Toast.LENGTH_SHORT).show();
                Message obtain = Message.obtain();
                obtain.what=1;
                obtain.obj=json;
              //  handler.sendMessage(obtain);

            }
        });
    }


    private void queryFromServer(String address, final String type) {
       // getjson();
        Log.d("aaaaa",address+" type "+type);
        showProgressDiaglog();
        OkHttpClient client =new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.d("aaaaa",responseText);
                boolean result = false;
                if ("province".equals(type)) {
                    Log.d("aaaaa","province");
                    result = Utility.handleProvinceResponse(responseText);

                } else if (type.equals("city")) {
                    result = Utility.handleCityResponse(responseText, selectProvince.getId());
                } else if (type.equals("county")) {
                    result = Utility.handleCountyResponse(responseText, selectCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if (type.equals("province")) {
                                queryProvinces();
                            } else if (type.equals("city")) {
                                queryCities();
                            } else if (type.equals("county")) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       closeProgressDialog();
                        Log.d("aaaaa","失败失败失败失败失败");
                        Toast.makeText(getContext(), "加载失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

    private void showProgressDiaglog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
