package com.shanlu.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class JsonUtil {

    public static Boolean verifyCurrency(String FileContent,String currency){


        //加载文件
//            FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/src/main/java/com/shanlu/data/appLogin.json");
        //读取文件内容

//            File file = new File(System.getProperty("user.dir") + "/src/main/java/com/shanlu/data/appLogin.json");
//            String JsonString = FileUtils.readFileToString(file, "UTF-8");

        Gson gson = new Gson();
        HashMap hashMap = gson.fromJson(FileContent, HashMap.class);
        Set currencys = hashMap.keySet();
        return currencys.contains(currency);

    }

    public static String getInputParamByCurrency(String FileContent,String currency){
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(FileContent, JsonObject.class);

        System.out.println(jsonObject);
//        return hashMap.get(currency);
        String asString = jsonObject.get(currency).toString();

        return asString;


    }

    public static void main(String[] args) {
        String FileContent="{\"SG\":{\"a\":\"eee\",\"b\":\"www\"},\"TH\":{\"a1\":\"eee1\",\"b1\":\"www1\"}}";
        String currency="SG";
        System.out.println(getInputParamByCurrency(FileContent, currency));
    }

}
