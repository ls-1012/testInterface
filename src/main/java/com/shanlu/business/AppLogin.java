package com.shanlu.business;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shanlu.util.JsonUtil;
import com.shanlu.util.RestClient;
import com.shanlu.util.TestUtil;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class AppLogin {

    String currency;
    CloseableHttpResponse appLogin;

    public AppLogin(String currency) {
        this.currency = currency;
        appLogin = appLogin();
    }

    public CloseableHttpResponse appLogin() {
        //登陆接口header还需要加入
        /*
        {"Content-Type":"application/json",
        "User-Agent":"",
        "X-Real-IP	":"",
        "Device-Id	":"",
        "x-user-agent	":""
        }
         */
//        HashMap<String, String> header = new HashMap<>();
        String loginUrl = TestUtil.getAppHostName() + "/api/login";


        String header = "{\"Content-Type\":\"application/json\"," +
                "        \"User-Agent\":\"okhttp/4.9.0\"," +
                "        \"X-Real-IP\":\" \"," +
                "        \"Device-Id\":\"0ba6a339-fd86-45f9-bb1a-36a796da4068\"," +
                "        \"x-user-agent\":\"Apaylater (Android; OPPO PEGM00; 30; zh) uuid/0ba6a339-fd86-45f9-bb1a-36a796da4068 adid/79f617c3-a3d4-490e-b980-c98376b46a49 version/3.11.0+80\"}";

        try {
            String appLoginData = TestUtil.getJsonStringFromFile(new File(System.getProperty("user.dir") + "/src/main/java/com/shanlu/data/appLogin.json"));
//            System.out.println("获取到入参为： "+appLoginData);
            if (!JsonUtil.verifyCurrency(appLoginData, currency)) return null;

            String entityString = JsonUtil.getInputParamByCurrency(appLoginData, currency);
//            System.out.println("app login入参为： "+entityString);
//            JSONObject entityJson = TestUtil.getJsonObjectByString(entityString);
////            entityJson.put("mobileNumber",mobile);
//            entityString = JSONObject.toJSONString(entityJson);
//            header.put("Content-Type", "application/json");
            HashMap<String, String> headerMap = new Gson().fromJson(header, HashMap.class);

//            System.out.println("app 登录入参： "+entityString);
//            System.out.println("app 登录header： "+headerMap);

            CloseableHttpResponse response = RestClient.post(loginUrl, entityString, headerMap);
            return response;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getAppToken() {
//        CloseableHttpResponse response = appLogin();
        if (appLogin != null) {
            JsonObject responseBody = TestUtil.getResponseBody(appLogin);
            System.out.println("登陆结果返回：" + responseBody);
            return TestUtil.getValueByJPath(responseBody, "data/JWT");
        }
        return null;
    }

    //拼装app端的header
    private HashMap<String, String> getAppHeader() {
        HashMap<String, String> header = new HashMap<>();
        String appToken = getAppToken();
        header.put("cookie", "X-ADV-TOKEN=" + appToken);
        String host = TestUtil.getAppHostName().split("//")[1];
        header.put("Host", host);
        header.put("Content-Type", "application/json");
        header.put("x-user-agent", "Apaylater (iOS; iPhone 8; 15.1; zh-Hans-SG) uuid/579d6678-c1ea-4829-8664-b80750322a2d version/3.17.0+450");
        header.put("device-id", "579d6678-c1ea-4829-8664-b80750322a2d");
        System.out.println("appHeader:" + header);
        return header;
    }

    //app 提供的create payment接口
    private CloseableHttpResponse createPayment(HashMap<String, String> hashMap, String currency, String outletId) {
        String url = TestUtil.getAppHostName() + "/api/payment?amount=5&currency=" + currency + "&consents=&merchantId=" + outletId;
        System.out.println("create payment 接口的请求地址是： " + url);
        try {
            CloseableHttpResponse response = RestClient.get(url, hashMap);
            return response;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CloseableHttpResponse getOrderPlans(HashMap<String, String> hashMap, String paymentId) {
        String url = TestUtil.getAppHostName() + "/api/order-plans?appActionSource=QRCODE&atomePlusPoints=0&paymentId=" + paymentId + "&voucherUserRecordIds=";
        System.out.println("order plans 接口的请求地址是： " + url);
        try {
            CloseableHttpResponse response = RestClient.get(url, hashMap);
            return response;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //支付接口
    public CloseableHttpResponse payOrder(String redirectUrl) {
        /**
         * 支付成功之前，需要拿已存在的用户的数据，需要下面三个数据
         *
         * request header
         *      "cookie":"X-ADV-TOKEN=eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJBQUNMVUIiLCJzdWIiOiJ7XCJ1c2VySWRcIjpcIlU5OTU5MTAwNDk2XCJ9IiwiYXVkIjoiQVBQIiwiaWF0IjoxNjM3ODEwMDEzLCJleHAiOjE2MzkwMTk2MTN9.D3DPLYQEV3BbZGDmdjzy890LyXkAW643b5OwV-cuAsgWLsUz32dy5fsnMqjQNc1b8P6jLOOUXIqA33mWCnh19A; _ga_71DNSRTZXY=GS1.1.1637751333.2.1.1637751445.0; _ga=GA1.1.1916318478.1637735874; _atome_webAdvertisingId=9ce78b59-5944-4be2-8064-84f60991be68; _atome_webDeviceId=7fc361c8-3c9f-49f8-92d6-36b44007ad58"
         *      "x-user-agent":"Apaylater (iOS; iPhone XR; 13.6.1; en-US) uuid/d13acbad-1315-4c53-94b7-97bcf9817316 version/2.14.0+385"
         *      "advertising-id":"BD8536DA-FBFE-4D57-A90F-D397F764838E"
         *      "device-id":"d13acbad-1315-4c53-94b7-97bcf9817316"
         *      "user-agent":"Paylater MY/2.14.0 (my.atome.paylater.debug; build:385; iOS 13.6.1) Alamofire/5.4.3"
         *      "accept-language":"en"
         * "cardId": "",
         * "paymentId": "",
         * "productId": "",
         *
         */
        JsonObject entityJson = null;
        String entityString;
        try {
            entityString = TestUtil.getJsonStringFromFile(new File(System.getProperty("user.dir") + "/src/main/java/com/shanlu/data/payOrder.json"));
//            System.out.println("从文件获取到的下单入参： "+entityString);
            entityJson = TestUtil.getJsonObjectByString(entityString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, String> headerMap = getAppHeader();
        if (headerMap == null) {
            throw new RuntimeException("请求头参数为空！！！请检查参数～～～");
        }
        String cardId = null;
        String paymentId = getPaymentId(redirectUrl);
        String productId = getProductId(redirectUrl);

        if (entityJson.get("cardId") != null) {
            cardId = getCardId(headerMap);
            if (cardId.isEmpty()) {
                throw new RuntimeException("下单请求参数错误，下单失败！\n请求参数是 ：" + "cardId：" + cardId);

            }
        }


        if (paymentId.isEmpty() || productId.isEmpty()) {
            throw new RuntimeException("下单请求参数错误，下单失败！\n请求参数是 ：" + "paymentId：" + paymentId + "productId；" + productId);
        }
//        gateway生成的二维码不需要调用这个接口
//        //createPayment 接口调用
//        JsonObject createPaymentResult = TestUtil.getResponseBody(createPayment(headerMap,outletId,currency));
//        System.out.println("createPayment接口返回结果: "+createPaymentResult);
//
//        String createPaymentResultCode = TestUtil.getValueByJPath(createPaymentResult, "code");
//        System.out.println("orderPlansResultCode返回结果： "+createPaymentResultCode);
//        if(!createPaymentResultCode.equalsIgnoreCase("success")){
//            throw new RuntimeException("orderPlans接口返回异常，请检查参数～～～");
//        }


        //orderPlans 接口调用
//        JsonObject orderPlansResult = TestUtil.getResponseBody(getOrderPlans(headerMap, paymentId));
        JsonObject orderPlansResult = TestUtil.getResponseJson(getOrderPlans(headerMap, paymentId));
        System.out.println("orderplans接口返回结果: " + orderPlansResult);
        String orderPlansResultCode = TestUtil.getValueByJPath(orderPlansResult, "code");
        System.out.println("orderPlansResultCode返回结果： " + orderPlansResultCode);
        if (orderPlansResultCode.equalsIgnoreCase("INVALID_MAX_SPEND")) {
            throw new RuntimeException("orderPlans接口返回异常，请检查该用户状态是否正确或者下单金额是否合法～～～");
        }

        //orders 接口调用
        entityJson.addProperty("cardId", cardId);
        entityJson.addProperty("paymentId", paymentId);
        entityJson.addProperty("productId", productId);
        entityString = entityJson.toString();
        String url = TestUtil.getAppHostName() + "/api/orders";
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + entityString);
//        System.out.println("下单接口的url： "+url);
//        System.out.println("下单接口的入参： "+entityString);
//        System.out.println("下单接口的header： "+headerMap);
        try {
            CloseableHttpResponse response = RestClient.post(url, entityString, headerMap);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


    private CloseableHttpResponse getPublicResponseByRedirectUrl(String redirectUrl) {
//        CloseableHttpResponse payment = transactionService.createPaymentFromMC(outlet);
//        JSONObject paymentBody = TestUtil.getResponseBody(payment);
//        String redirectUrl = TestUtil.getValueByJPath(paymentBody, "data/redirectUrl");
//        System.out.println("redirectUrl 为： "+redirectUrl);
        String token = redirectUrl.split("\\?")[1];
        String publicUrl = "https://" + redirectUrl.split("/")[2] + "/api/payment/public?" + token;
        System.out.println("获取payment/public接口的地址： " + publicUrl);

        //请求public 接口
        try {
            CloseableHttpResponse publicResponse = RestClient.get(publicUrl, null);
            return publicResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


//    public String getOrderIdByPayResult(){
//        CloseableHttpResponse response = getPublicResponseByRedirectUrl();
//        JsonObject responseJson = TestUtil.getResponseBody(response);
//        //通过jsonPath 获取orderid
//        String orderid = TestUtil.getValueByJPath(responseJson, "");
//        return orderid;
//    }

    //通过查询接口获取用户下的cardid
    private String getCardId(HashMap<String, String> headerMap) {
        String url = TestUtil.getAppHostName() + "/api/payment-methods";

        try {
            CloseableHttpResponse response = RestClient.get(url, headerMap);
            JsonObject responseBody = TestUtil.getResponseBody(response);
            System.out.println("获取payment method列表的相应结果： " + responseBody);
            String cardId = TestUtil.getValueByJPath(responseBody, "data/cards[0]/aaclubPaymentInstrumentId");
            if (!cardId.isEmpty())
                System.out.println("cardId: " + cardId);
            return cardId;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private String getPaymentId(String redirectUrl) {
        /*
        1、通过之前create payment后生成paymentid(online 和offline outlet)
        2、调用openapi create payment接口生成paymentid  已实现
        // 3、通过payment接口创建offline的paymentid
         */

        CloseableHttpResponse response = getPublicResponseByRedirectUrl(redirectUrl);
        if (null != response) {
            JsonObject responseJson = TestUtil.getResponseBody(response);

            String paymentId = TestUtil.getValueByJPath(responseJson, "data/paymentId");
            System.out.println("paymentId: " + paymentId);
            return paymentId;
        }
        return null;

    }


    private String getProductId(String redirectUrl) {
        CloseableHttpResponse response = getPublicResponseByRedirectUrl(redirectUrl);
        JsonObject responseJson = TestUtil.getResponseBody(response);
        String productId = TestUtil.getValueByJPath(responseJson, "data/plans[0]/productId");
        System.out.println("productId: " + productId);
        return productId;
    }

}
