package com.shanlu.util;

//import org.testng.internal.Yaml;


import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class YamlReader {

    public static String COUNTRY;
    private static Map<String, Map<String, Object>> properties = new HashMap<>();

//    public static final YamlReader instance = new YamlReader();

    static {
        Yaml yaml = new Yaml();
        try (InputStream in = YamlReader.class.getClassLoader().getResourceAsStream("application.yml");) {
//            System.out.println("inputStream: " + in);
            properties = yaml.loadAs(in, HashMap.class);
//            Map<String, Object> sg = properties.get("SG");
//            System.out.println(properties);
//            System.out.println(sg);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static Boolean verifyCurrency(String currency) {
        //获取第一层级
        Set<String> currencys = properties.keySet();//[SG, mybatis, MY]
        return currencys.contains(currency);
    }

    public static String getHostByCurrency(String hostName) {
//        System.out.println("COUNTRY : " + COUNTRY);
//        System.out.println("properties: " + properties);
        return String.valueOf(properties.get(COUNTRY).get(hostName));

    }

//    public static String getValueByKey(String key) {
//        String separator = ".";
//        String[] separatorKeys;
//        String value = "";
//        if (key.contains(separator)) {
//            separatorKeys = key.split("\\.");
//
//            Map<String, String> finalValue = new HashMap<>();
//
//            for (int i = 0; i < separatorKeys.length; i++) {
//                if (i == 0) {
//                    finalValue = (Map) properties.get(separatorKeys[i]);
////                System.out.println("==0时的fianlValue: "+finalValue);
//                    continue;
//                } else {
//                    value = finalValue.get(separatorKeys[i]);
//                }
//                if (finalValue == null || value == null) {
//
//                    throw new RuntimeException("请检查传入的 " + separatorKeys[i] + " 是否存在！！");
//                }
//
//            }
//
//        } else {
//            throw new RuntimeException("请检查传入的 " + key + " 是否正确！！");
//        }
//        return value;
//    }


    public static void main(String[] args) {
//        String valueByKey = getValueByKey("SG.HOST");
////        Object valueByKey1 = getValueByKey("SG.AAA");
//        Object valueByKey2 = getValueByKey("TH");
//        System.out.println(valueByKey);
////        System.out.println(valueByKey1);
//        System.out.println(valueByKey2);
        YamlReader yamlReader = new YamlReader();
        System.out.println(verifyCurrency("SG"));


    }

}
