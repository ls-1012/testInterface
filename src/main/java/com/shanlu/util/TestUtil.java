package com.shanlu.util;

//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
import com.google.gson.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.File;


import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import static org.apache.http.auth.AuthProtocolState.SUCCESS;


public class TestUtil {
    /**
     *
     * @param responseJson 这个变量是拿到响应字符串通过json转换成json对象
     * @param jpath 需要查询json对象的值的路径写法
     * jpath写法举例：1） per_page 2) data[1]/first_name,data是一个json数组，[1]表示索引
     *              /first_name 表示data数组下某一个元素下的json对象的名称为first_name
     * @return 返回first_name这个json对象名称对应的值
     */
    //json解析方法
    public static String getValueByJPath(JsonObject responseJson, String jpath){
        Object obj = responseJson;
//        System.out.println("responseJson的值为："+responseJson+",jpath 为："+jpath);
        if(jpath!=null && !jpath.isEmpty() && jpath.contains("/")){
            for(String s:jpath.split("/")){
                if(null == obj){
                    System.out.println("请确认请求body：「"+responseJson + "」，或者请求参数「" + s + "」是否正确");
                    return null;
                }
                if(!s.isEmpty()){
                    if(!(s.contains("[")||s.contains("]"))){
                        obj = ((JsonObject) obj).get(s);
                    }else if(s.contains("[")||s.contains("]")){
                        obj = ((JsonArray)((JsonObject)obj).get(s.split("\\[")[0])).get(Integer.parseInt(s.split("\\[")[1].replaceAll("]","")));
                    }
                }
            }

            return ((JsonElement)obj).getAsString();


        } else if(jpath != null){
            return (((JsonObject)obj).get(jpath)).getAsString();

        }else{
            throw new RuntimeException("jpath: " + jpath + "为空，抛出异常！！！");
        }


    }

    public static int getStatusCode(CloseableHttpResponse httpResponse){
        if(null!=httpResponse){
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            return statusCode;
        }
        throw new RuntimeException("httpResponse is null");


    }

    /**
     *
     * @param httpResponse
     * @return
     *
     *
     */
    public static JsonObject getResponseJson(CloseableHttpResponse httpResponse) {
        if(null!=httpResponse){
            JsonObject responseJson=null;
            HttpEntity entity = httpResponse.getEntity();
//            System.out.println("HttpEntity entity: "+entity);
            try {
                String resEntityString = EntityUtils.toString(entity);
                System.out.println("resEntityString: " +resEntityString);
                if(resEntityString!=null && !resEntityString.isEmpty()){
                    JsonParser parser = new JsonParser();
                    JsonElement resElement = parser.parse(resEntityString);
                    responseJson = resElement.getAsJsonObject();
                    System.out.println("返回的response body的json格式："+responseJson);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
                return responseJson;
        }
        return null;
    }


    public static String getJsonStringFromFile(File file) throws IOException {
        System.out.println(file.getPath());
        System.out.println(file.getName());
        FileReader fileReader = new FileReader(file);

        String tmp="";
        char[] cbuf = new char[100];
        int n;
//        StringBuilder stringBuilder= new StringBuilder();
        while((n = fileReader.read(cbuf)) != -1){
//            stringBuilder.append(n);
            tmp += new String(cbuf,0,n);

        }
        return tmp;

    }

//
//    public static String setCountryLine(){
//        return "SG";
//    }
    public static String getHostName(){
//        YamlReader yamlReader = new YamlReader();
//        String Env = setCountryLine();
        String hostName = YamlReader.getHostByCurrency("HOST");
//        String hostName = (String)YamlReader.getValueByKey(Env + ".HOST");
        return hostName;
    }

    public static String getAppHostName(){
//        TestBase testBase = new TestBase();
//        String appHost = testBase.prop.getProperty("APPHOST");
//        String Env = setCountryLine();
        String appHost = (String)YamlReader.getHostByCurrency( "APPHOST");
        return appHost;
    }

    public static String getMbossHostName(){
//        TestBase testBase = new TestBase();
//        String mbossAppHost = testBase.prop.getProperty("MBOSSHOST");
//        String Env = setCountryLine();
        String mbossAppHost = (String)YamlReader.getHostByCurrency( "MBOSSHOST");
        return mbossAppHost;
    }

    public static String getCurrency(){
//        TestBase testBase = new TestBase();
//        String currency = testBase.prop.getProperty("currency");
//        String Env = setCountryLine();
        String currency = (String)YamlReader.getHostByCurrency( "currency");
        return currency;
    }

    public static void getResponseHeader(CloseableHttpResponse response){
        JsonObject responseJson=null;

        if (null != response) {
            String location = response.getLastHeader("location").getValue();
            System.out.println("location: "+location);

        }

    }
    //将CloseableHttpResponse中的body返回
    public static JsonObject getResponseBody(CloseableHttpResponse response){

        JsonObject responseJson=null;
        try {
            if (null != response) {
                InputStream contentInputStream = response.getEntity().getContent();
                String contentString = IOUtils.toString(contentInputStream, "utf-8");
                if(contentString!=null && !contentString.isEmpty()){
                    JsonParser parser = new JsonParser();
                    JsonElement resElement = parser.parse(contentString);
                    responseJson = resElement.getAsJsonObject();
                    System.out.println("返回的response body的json格式："+responseJson);
                    return responseJson;
                }

            }else{
                throw new Exception("response 为空，获取不到content！！");}
        }catch (Exception e) {
                e.printStackTrace();
        }
        return null;
    }

    public static String getRandomEcommId(){
        /*
        模拟生成10为随机的字母+数字的字符串
         */
        String randomAlphanumeric= RandomStringUtils.randomAlphanumeric(10);
//        System.out.println(randomAlphanumeric);
        return randomAlphanumeric;
    }

    public static String getRandomRefundId(){
        String s = RandomStringUtils.randomAlphabetic(1);
        String s1 = RandomStringUtils.randomNumeric(13);
        return s+s1;
    }

    //生成随机companyId
    public static String getRandomComRegNumberId(){
        String s = "shanlu_test";
        String s1 = MyDateUtil.getCurrentTimeStamp();
        return s+s1;
    }

    public static String getRandomMerBrandId(){
        String s = "shanlu_brand";
        String s1 = MyDateUtil.getCurrentTimeStamp();
        return s+s1;
    }


    public static String getReferenceId(){
        String s = RandomStringUtils.randomAlphabetic(1);
        String s1 = RandomStringUtils.randomNumeric(15);
        return s+s1;
    }

    public static JsonObject getJsonObjectByString(String entityString){
        if(entityString!=null && !entityString.isEmpty()){
            JsonParser parser = new JsonParser();
            JsonObject entityJson = parser.parse(entityString).getAsJsonObject();
            return entityJson;
        }

        return null;
    }

    public static String base64Encode(Pair<String,String> of){
        String key = of.getKey();
        String value = of.getValue();
        Base64.Encoder encoder = Base64.getEncoder();
        return "Basic "+encoder.encodeToString((key+":"+value).getBytes(StandardCharsets.UTF_8));

    }

    public static String createRefundId(){
        /**
         * 生成一个库里不存在的refundId
         */
        String randomRefundId = TestUtil.getRandomRefundId();

        return randomRefundId;
    }


    public static String formatParam(JsonObject jsonObject){
        Gson gson = new Gson();
        return gson.toJson(jsonObject);
    }



    public static JsonObject formatParam(String value){
        Gson gson = new Gson();
        return gson.fromJson(value,JsonObject.class);
    }






}
