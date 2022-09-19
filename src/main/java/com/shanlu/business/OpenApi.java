package com.shanlu.business;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shanlu.config.Path;
import com.shanlu.mapper.MerchantMapper;
import com.shanlu.util.RestClient;
import com.shanlu.util.TestUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class OpenApi {
    String currency;
    MbossLogin mbossLogin;
    McLogin mcLogin;
    AppLogin appLogin;
    String referenceId;
    MerchantMapper merchantMapper;

    public OpenApi(String currency,String cookie){
        this.currency=currency;
        mbossLogin = new MbossLogin(cookie,currency);
        mcLogin = new McLogin(currency);
        appLogin = new AppLogin(currency);
        merchantMapper = new MerchantMapper();

    }

    public HashMap<String,String> getOpenAPIHeader(String cookie, String outletId){
        Pair<String, String> accessKeyAndPAsswordByMerchant = mbossLogin.getAccessKeyAndPAsswordByMerchant(cookie, outletId);
        HashMap<String,String> header = new HashMap<>();
        String token = TestUtil.base64Encode(accessKeyAndPAsswordByMerchant);

        header.put("Authorization",token);
        header.put("Content-Type", "application/json");
        return header;

    }

    public CloseableHttpResponse createPaymentViaOpenAPI(String cookie, String outletId, String orderAmount) {


        String url = Path.OPENAPICPAYMENT;

        //调用info接口的返回结果拿到key 和password
//        Pair<String, String> accessKeyAndPAsswordByMerchant = mbossService.getAccessKeyAndPAsswordByMerchant(cookie, outletId);
        //header 需要access key 和password通过 basic auth加密后传入header的Authorization字段
        HashMap<String, String> openAPIHeader = getOpenAPIHeader(cookie, outletId);

        //从文件中获取请求入参。然后拼装参数
        String entityString = "";
        try {
            entityString = TestUtil.getJsonStringFromFile(new File(System.getProperty("user.dir") +"/src/main/java/com/shanlu/data/openAPICreatePayment.json"));
            System.out.println("从文件中获取到的入参是： "+entityString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将string转为Json
        JsonObject entityJson = TestUtil.getJsonObjectByString(entityString);
        System.out.println("转为json格式： "+entityJson);

        /*
        currency 构造币种，配置文件中读取
        referenceId 不可重复，可使用EcommerceID
        amount 取最小金额
         */
        String currency = TestUtil.getCurrency();
        referenceId = TestUtil.getReferenceId();
//        System.out.println("currency的值是：  &&&&&  "+currency);
        entityJson.addProperty("currency",currency);
        entityJson.addProperty("referenceId",referenceId);
        entityJson.addProperty("amount",orderAmount);

        entityString = entityJson.toString();
        System.out.println("创建payment的入参是： "+entityString);
        try {
            CloseableHttpResponse response = RestClient.post(url, entityString, openAPIHeader);
            System.out.println("createPaymentViaOpenAPI 返回response "+response);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getReferenceIdByPaymentId(String paymentId){
        if(!paymentId.isEmpty()){
            referenceId = merchantMapper.getReferenceIdByPaymentId(paymentId);
            if(!referenceId.isEmpty()){
                return referenceId;
            }

        }
        return "";
    }


    private String getRedirectUrlViaOpenAPI(JsonObject responseBody){

        System.out.println("getRedirectUrlFromOpenAPI# : "+responseBody);
        if(!"processing".equalsIgnoreCase(TestUtil.getValueByJPath(responseBody,"status"))){
            return null;
        }
        String redirectUrl = TestUtil.getValueByJPath(responseBody, "redirectUrl");
        return redirectUrl;
    }

    public CloseableHttpResponse getPaymentInfo(String mbossCookie,String outletId){
        /**
         * paymentInfo结构：
         * {
         * "referenceId": "P1293201980299030",
         * "currency": "SGD",
         * "amount": 12010,
         * "refundableAmount": 0,
         * "status": "PROCESSING",
         * "refundTransactions": [ ],
         * "merchantReferenceId": "P1293201980299030",
         * "redirectUrl": "https://gateway.apaylater.com/payments/hCTwVplvXTXlyqpuUogb",
         * "appPaymentUrl": "https://app.apaylater.com/entry?q=eyJpZCI6IlA2NDAyMjc4NzkwNUY1MDk4NjI4OThFOTAwMDAxM0FFMzEzIiwidHlwZSI6IlBBWV9UT19QQVlNRU5UIiwic3RhdHVzIjoiRU5BQkxFRCIsIm1lc3NhZ2UiOiIiLCJjb3VudHJ5Q29kZSI6IlNHIn0="
         * }
         */
        HashMap<String, String> openAPIHeader = getOpenAPIHeader(mbossCookie, outletId);
        String url = "https://api.apaylater.net/v2/payments/"+referenceId;
        try {
            CloseableHttpResponse paymentInfo = RestClient.get(url, openAPIHeader);
            return paymentInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //用app支付的方法
    public JsonObject PPayOrder(String mbossCookie, String outletId,String orderAmount) {

        CloseableHttpResponse paymentViaOpenAPI = createPaymentViaOpenAPI(mbossCookie,outletId,orderAmount);
        JsonObject paymentViaOpenAPIJson = TestUtil.getResponseBody(paymentViaOpenAPI);
        System.out.println("paymentViaOpenAPI的response： " + paymentViaOpenAPIJson);

        String redirectUrlViaOpenAPI = getRedirectUrlViaOpenAPI(paymentViaOpenAPIJson);
        System.out.println("redirectUrlViaOpenAPI: " + redirectUrlViaOpenAPI);
        if(redirectUrlViaOpenAPI.isEmpty()) {
            System.out.println("生成payment失败～～～，请检查环境！！！");
            return null;
        }

        //模拟手机扫描gateway生成的二维码进行支付
        String currency = TestUtil.getCurrency();
        CloseableHttpResponse payOrderResultResponse = appLogin.payOrder(redirectUrlViaOpenAPI);
        JsonObject responseBody = TestUtil.getResponseBody(payOrderResultResponse);
        if (payOrderResultResponse != null) {
            System.out.println("支付返回结果：" + responseBody);
            return responseBody;
        }

        return null;
    }


    public CloseableHttpResponse refundViaAPI(String mbossCookie, String outletId, String refundAmount) {
        String entityString = null;
        try {
            //获取退款入参
            entityString = TestUtil.getJsonStringFromFile(new File(System.getProperty("user.dir")+"/src/main/java/com/shanlu/data/refundForAPI.json"));
            System.out.printf("openAPI退款原参数: " + entityString);
            //入参类型转换为json格式;
            JsonObject entityJson = TestUtil.formatParam(entityString);

            HashMap<String, String> header = getOpenAPIHeader(mbossCookie,outletId);
//            //获取创建payment的referenceId
//            CloseableHttpResponse payment = createPaymentViaOpenAPI(mbossCookie,outletId,refundAmount);
//            JsonObject paymentResBody = TestUtil.getResponseBody(payment);
//            String redirectUrl = getRedirectUrlFromOpenAPI(paymentResBody);
//
//            System.out.println("创建payment 的response： "+paymentResBody);
//            System.out.println("redirectUrl: "+redirectUrl);

//            String referenceId = TestUtil.getValueByJPath(paymentResBody, "referenceId");
            System.out.println("创建 openAPI payment referenceId： "+referenceId);


//            //支付
//            String currency = TestUtil.getCurrency();
//            CloseableHttpResponse payOrderResultResponse = appLogin.payOrder(outletId,currency,redirectUrl);
//            int payOrderStatusCode = TestUtil.getStatusCode(payOrderResultResponse);
//            JsonObject payOrderResult = TestUtil.getResponseBody(payOrderResultResponse);
//            System.out.println("支付结果： " + payOrderResult);
//            String code = TestUtil.getValueByJPath(payOrderResult, "code");
//            if(!"success".equalsIgnoreCase(code)){
//                System.out.println("支付失败，请检查参数～～～" );
//                throw new RuntimeException("支付失败： "+payOrderResult);
//            }
//
//            //通过paymentInfo接口获取payment可退金额
//            CloseableHttpResponse paymentInfo = getPaymentInfo(mbossCookie,outletId,referenceId);
//            JsonObject paymentInfoResBody = TestUtil.getResponseBody(paymentInfo);
//
//            Double refundableAmount = Double.parseDouble(TestUtil.getValueByJPath(paymentInfoResBody, "refundableAmount"));

            //生成refundId
            String refundId = TestUtil.createRefundId();
            double refund_Amount = Double.parseDouble(refundAmount);
            if(refund_Amount > 0){
                //拼装入参
                entityJson.addProperty("refundAmount", refund_Amount);
                entityJson.addProperty("refundId", refundId);
            }else{
                System.out.println("最大可退款金额为0，不支持退款!!!! " + refund_Amount);
                return null;
            }

            //发起退款
            String url = Path.OPENAPICPAYMENT+"/"+referenceId+"/refund";
            //入参数据格式化
            entityString = TestUtil.formatParam(entityJson);
            System.out.println("openAPI退款实际请求参数是： "+entityString);


            System.out.println("URL:"+url);
            System.out.println("header:"+header);
            CloseableHttpResponse response = RestClient.post(url, entityString, header);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
