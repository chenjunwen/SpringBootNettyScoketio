package com.ymy;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
public class TempTest {
    public static void main(String[] args) {
        JSONArray buildingArr = new JSONArray();
        JSONObject obj1 = new JSONObject();
        obj1.put("age","10");
        obj1.put("name","JSon");
        obj1.put("type", "1");

        JSONObject obj2 = new JSONObject();
        obj2.put("age","20");
        obj2.put("name","haha");
        obj2.put("type", "1");

        JSONObject obj3 = new JSONObject();
        obj3.put("age","30");
        obj3.put("name","tttt");
        obj3.put("type", "2");

        JSONObject obj4 = new JSONObject();
        obj4.put("age","40");
        obj4.put("name","rty");
        obj4.put("type", "0");

        buildingArr.add(obj1);
        buildingArr.add(obj2);
        buildingArr.add(obj3);
        buildingArr.add(obj4);
        long timeMillis = System.currentTimeMillis();
        JSONObject obj = new JSONObject();
        buildingArr.forEach(item->{
            JSONObject json = (JSONObject) item;
            String type = json.getString("type");
            JSONArray jsonArray = obj.getJSONArray(type);
            if(jsonArray == null){
                jsonArray = new JSONArray();
            }
            jsonArray.add(item);
            obj.put(type, jsonArray);
        });
        long timeMillis1 = System.currentTimeMillis();
        System.out.println(timeMillis1- timeMillis);




    }
}
