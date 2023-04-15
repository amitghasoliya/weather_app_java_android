package com.example.weatherapp;

import org.json.JSONException;
import org.json.JSONObject;

public class weatherData {
    private String temperature, icon, city, weatherType;
    private int condition;

    public static weatherData fromJson(JSONObject jsonObject){
        try{
            weatherData weatherD=new weatherData();
            weatherD.city=jsonObject.getString("name");
            weatherD.condition=jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherD.weatherType=jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            weatherD.icon=updateWeatherIcon(weatherD.condition);
            double tempResult=jsonObject.getJSONObject("main").getDouble("temp")-273.15;
            int roundedValue=(int)Math.rint(tempResult);
            weatherD.temperature=Integer.toString(roundedValue);
            return weatherD;

        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String updateWeatherIcon(int condition){
        if (condition>=0 &&condition<=300){
            return "thunderstorm";
        }
        else if (condition>=300 &&condition<=500){
            return "lightrain";
        }
        else if (condition>=500 &&condition<=600){
            return "shower";
        }
        else if (condition>=600 &&condition<=700){
            return "snow2";
        }
        else if (condition>=701 &&condition<=771){
            return "fog";
        }
        else if (condition>=772 &&condition<=800){
            return "overcast";
        }
        else if (condition==800){
            return "sunny";
        }else if (condition>=801 &&condition<=804){
            return "cloudy";
        }
        else if (condition>=900 &&condition<=902){
            return "thunderstorm";
        }else if (condition==903){
            return "snow1";
        }
        else if (condition==904){
            return "sunny";
        }
        else if (condition>=905 &&condition<=100){
            return "thunderstorm";
        }
        return "dunno";
    }

    public String getTemperature() {
        return temperature+"°C";
    }

    public String getIcon() {
        return icon;
    }

    public String getCity() {
        return city;
    }

    public String getWeatherType() {
        return weatherType;
    }
}
