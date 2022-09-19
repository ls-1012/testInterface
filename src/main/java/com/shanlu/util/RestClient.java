package com.shanlu.util;



import com.google.gson.JsonObject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


//实现get、post等方法
public class RestClient {

    /**
     * 不带请求头
     * @param url
     * @return 返回响应对象
     * @throws IOException
     */
    public static CloseableHttpResponse get(String url) throws IOException {
        //创建一个可关闭的HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建一个httpGet的请求对象
        HttpGet httpGet = new HttpGet(url);
//        Log.info("开始发送get请求。。。");
        //发起请求，并接收返回对象
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
//        Log.info("发送请求成功！开始获得响应对象。");
        return httpResponse;
    }



    /**
     * 带请求头的get
     * @param url
     * @param headermap 键值对形式请求头
     * @return 返回响应对象
     * @throws IOException
     */
    public static CloseableHttpResponse get(String url,HashMap<String,String> headermap) throws IOException {
        //创建一个可关闭的HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
//        System.out.println("get...方法开始执行了。。。。。。");
        HttpGet httpGet = new HttpGet(url);
//        Log.info("开始发送get请求。。。");
        //添加请求头
        if(headermap==null){
            httpGet.addHeader("Content-Type","application/json");
        }else{
            for(Map.Entry<String,String> entry:headermap.entrySet()){
                httpGet.addHeader(entry.getKey(),entry.getValue());
            }
        }
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
//        System.out.println("get...方法开始执行结束。。。。。。");
//        System.out.println("打印返回结果！！！" + TestUtil.getStatusCode(httpResponse));

        return httpResponse;
    }

    /**
     *
     * @param url
     * @param entityString post请求的json参数
     * @param headermap
     * @return
     * @throws IOException
     */
    public static CloseableHttpResponse post(String url,String entityString,HashMap<String,String> headermap) throws IOException {
        //创建一个可关闭的HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建一个httpPost的请求对象
        HttpPost httpPost = new HttpPost(url);
        //设置post的payload
        httpPost.setEntity(new StringEntity(entityString));

        System.out.println("post 请求开始～～～ ");
//        Log.info("开始发送post请求。。。");
        //添加请求头
        for(Map.Entry<String,String> entry:headermap.entrySet()){
            httpPost.addHeader(entry.getKey(),entry.getValue());
        }
        //发送post请求
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
//        Log.info("发送请求成功！开始获得响应对象。");
        return httpResponse;
    }

    public static int getStatusCode(CloseableHttpResponse httpResponse){
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        return statusCode;
    }

//
}
