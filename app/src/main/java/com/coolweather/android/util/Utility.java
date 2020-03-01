package com.coolweather.android.util;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    //解析和处理服务器返回的省级数据
    public static  boolean handleProvinceResponse(String response){
        try{
            JSONArray allProvinces =new JSONArray(response);
            for (int i =0;i<allProvinces.length();i++){
                JSONObject provinceObject=allProvinces.getJSONObject(i);
                Province province= new Province();
                province.setProvinceName(provinceObject.getString("name"));
                province.setProvinceCode(provinceObject.getInt("id"));
                province.save();
            }
            return  true;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return false;
    }

    //解析和处理服务器返回的市级数据
    public static  boolean handleCityResponse(String response,int provinceId){
        try{
            JSONArray allCyties =new JSONArray(response);
            for (int i =0;i<allCyties.length();i++){
                JSONObject cityObject=allCyties.getJSONObject(i);
                City city= new City();
                city.setCityName(cityObject.getString("name"));
                city.setCityCode(cityObject.getInt("id"));
                city.setProvinceId(provinceId);
                city.save();
            }
            return  true;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return false;
    }

    //解析和处理服务器返回的级数县据
    public static  boolean handleCountyResponse(String response,int cityId){
        try{
            JSONArray allCounties =new JSONArray(response);
            for (int i =0;i<allCounties.length();i++){
                JSONObject countyObject=allCounties.getJSONObject(i);
                County county= new County();
                county.setCountyName(countyObject.getString("name"));
                county.setWeatherId(countyObject.getString("weather_id"));
                county.setCityId(cityId);
                county.save();
            }
            return  true;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return false;
    }
    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            Weather weather=new Gson().fromJson(weatherContent,Weather.class);
            return weather;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
