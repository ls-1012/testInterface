package com.shanlu.business;


import com.google.gson.JsonObject;
import com.shanlu.config.Path;

import com.shanlu.entity.Order;
import com.shanlu.mapper.MerchantMapper;
import com.shanlu.util.*;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;


public class McLogin {

    String currency;
    MerchantMapper merchantMapper;
    AppLogin appLogin;
    JsonObject login;

    public McLogin(String currency) {
        this.currency = currency;
        merchantMapper = new MerchantMapper();
        appLogin = new AppLogin(currency);
        login = login();
    }


    public JsonObject login() {

        HashMap<String, String> header = new HashMap<>();

        String url = TestUtil.getHostName() + Path.LOGIN;
        System.out.println("login url: " + url);
        try {
            String loginData = TestUtil.getJsonStringFromFile(new File(System.getProperty("user.dir") + "/src/main/java/com/shanlu/data/login.json"));
//            System.out.println("获取到入参为： " + loginData);
            if (!JsonUtil.verifyCurrency(loginData, currency)) return null;

            //hard code
//            String entityString =  "{\"username\":\"company_lushan\", \"password\":\"changeMe123!\"}";
            String entityString = JsonUtil.getInputParamByCurrency(loginData, currency);
//            System.out.println("mc login 入参为： " + entityString);
            System.out.println(entityString);
            header.put("Content-Type", "application/json");
            CloseableHttpResponse response = RestClient.post(url, entityString, header);
//            int statusCode = TestUtil.getStatusCode(response);
            JsonObject login = TestUtil.getResponseBody(response);
//            System.out.println("#### /api/v2/login接口的返回结果为： " + responseJson);

//            token = TestUtil.getValueByJPath(responseJson, "data/token");
            return login;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getToken() {
//        CloseableHttpResponse response = login();

//        System.out.println("mc login 接口的返回结果：" + login);

        if (!"success".equalsIgnoreCase(TestUtil.getValueByJPath(login, "code"))) {
            throw new RuntimeException("mc 登录失败： " + login);
        }
        String token = TestUtil.getValueByJPath(login, "data/token");
        return token;

    }

    //拼装mc的header
    public HashMap<String, String> getHeader() {
        HashMap<String, String> header = new HashMap<>();
//        for(Map.Entry<String,String> entry:headermap.entrySet()){
//            header.put(entry.getKey(),entry.getValue());
//        }
        String token = getToken();

        header.put("Authorization", token);
        header.put("Content-Type", "application/json");
        return header;

    }
//    public Order findAmoutByTransactionID(String TransactionId) {
//        /**
//         * 1、获取数据库的例子,暂时没用
//         * 2、未实现多个库查询
//         */
////        Order order = new Order(TransactionId);
//
//        Order order = orderMapper.getAmountByTransactionId(TransactionId);
//        System.out.println("id: "+order.getId()+"amount: "+order.getAmount()+"refund_amount: "+order.getRefundAmount()
//            +"transaction_id: "+order.getTransactionId());
//
//        return order;
//    }


    public Double getMaxRefundAmountByTransactionDetail(CloseableHttpResponse response) {
//        Order order = new Order(TransactionId);

        JsonObject responseJson = TestUtil.getResponseBody(response);
        String code = responseJson.get("code").getAsString().toUpperCase();
        if ("SUCCESS".equals(code)) {
            Double maxRefundAmount = Double.parseDouble(TestUtil.getValueByJPath(responseJson, "data/maxRefundAmount"));

            return maxRefundAmount;
        }else if("NOT_FOUND".equals(code)){
            System.out.println("请检查请求数据是否允许发起该业务！！");
        }
        return null;
    }


    public CloseableHttpResponse getOverviewByAllOutlet(String startTime, String endTime, String... outletIds) {
        String url = null;
        HashMap<String, String> header = getHeader();
        if (null == startTime || startTime.isEmpty()) {
            url = TestUtil.getHostName() + "/api/v2/overview?startTime=";
        } else {
            String strateTimeStamp = MyDateUtil.getStrateTimeStamp(startTime);
            url = TestUtil.getHostName() + "/api/v2/overview?startTime=" + strateTimeStamp;
        }
        if (null == endTime || endTime.isEmpty()) {
            url = url + "&endTime=";
        } else {
            String endTimeStamp = MyDateUtil.getEndTimeStamp(endTime);
            url = url + "&endTime=" + endTimeStamp;
        }

        if (outletIds.length > 0) {
            url = url + "&selectedMerchantIds=";
            String tmp = null;
            for (String outletId : outletIds) {
                tmp += outletId + "%";

            }
            tmp = tmp.substring(0, tmp.length() - 1);
            url += tmp;
        }

        try {
            CloseableHttpResponse response = RestClient.get(url, header);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public CloseableHttpResponse getTransacitonsList(String startTime, String endTime, String orderId) {
        String url;
        if (null == startTime || startTime.isEmpty()) {
            System.out.println("startTime 不能为空");
            return null;
        } else {
            String stateTimeStamp = MyDateUtil.getStrateTimeStamp(startTime);
            url = TestUtil.getHostName() + "/api/v2/transactions?startTime=" + stateTimeStamp;
        }

        if (null == endTime || endTime.isEmpty()) {
            System.out.println("endTime 不能为空");
            return null;
        } else {
            String endTimeStamp = MyDateUtil.getEndTimeStamp(endTime);
            url += "&endTime=" + endTimeStamp;
        }
        url += "&limit=20&offset=0";
        HashMap<String, String> header = getHeader();
        if (!(null == orderId || orderId.isEmpty())) {
            url += "&orderId=" + orderId;
        }

        System.out.println("getTransaction的请求参数为： " + url);
        try {
            CloseableHttpResponse response = RestClient.get(url, header);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public CloseableHttpResponse getTransactionDetail(String TransactionId) {

        String url = TestUtil.getHostName() + "/api/v2/transaction?transactionId=" + TransactionId;
        System.out.println("TransactionDetail url: "+url);
        //获取token
        HashMap<String, String> header = getHeader();

        try {
            CloseableHttpResponse response = RestClient.get(url, header);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getOrderIdByTransactionId(String transactionId) {
        if (transactionId.isEmpty())
            return null;
        String orderId = getOrderIdByTransactionId(transactionId);
        System.out.println("getOrderIdByTransactionId# orderId: " + orderId + ",TransactionId: " + transactionId);
        return orderId;
    }


    public String getTransactionIdByOrderId(String orderId) {
        if (orderId.isEmpty())
            return null;
        String transactionId = getTransactionIdByOrderId(orderId);
        System.out.println("getTransactionIdByOrderId# orderId: " + orderId + ",TransactionId: " + transactionId);
        return transactionId;
    }

    public CloseableHttpResponse getWebConfig() {
        String url = TestUtil.getHostName() + Path.WEBCONFIG;

        HashMap<String, String> header = getHeader();

        try {
            CloseableHttpResponse response = RestClient.get(url, header);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Double getMinSpendByWebConfig() {
        CloseableHttpResponse webConfig = getWebConfig();
        JsonObject responseBody = TestUtil.getResponseBody(webConfig);
        String minSpend = TestUtil.getValueByJPath(responseBody, "data/minSpend");
        return Double.parseDouble(minSpend);

    }

    //通过mc create payment link接口生成paymentid，或者通过app的payment接口生成paymentid(忽略这种方式，稍微复杂)
    //老接口
    public CloseableHttpResponse createPaymentFromMC(String outletId, String orderAmount) {

        //这个接口可以支持online 和offline的outlet创建paymentid
        String url = TestUtil.getHostName() + "/api/v2/transactions/create";
        //获取token
        HashMap<String, String> header = getHeader();

        String entityString = "{\"amount\": 10, \"outletId\": \"S21C00150002\", \"eCommerceOrderId\": \"333322221111\"}";
        //将string转为Json
        JsonObject entityJson = TestUtil.getJsonObjectByString(entityString);

        String randomEcommId = TestUtil.getRandomEcommId();
        entityJson.addProperty("eCommerceOrderId", randomEcommId);

//        Double minSpend = getMinSpendByWebConfig();
//        entityJson.addProperty("amount", minSpend*5);
        entityJson.addProperty("amount", orderAmount);
        entityJson.addProperty("outletId", outletId);

        entityString = entityJson.toString();
        System.out.println("创建payment的入参是： " + entityString);
        try {
            CloseableHttpResponse response = RestClient.post(url, entityString, header);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getCurrentExpireTime(String outletId){
        String url = TestUtil.getHostName() + Path.CURRENTEXPIRETIME.replace("%",outletId);
        System.out.println("expiretime url: " +url );
        //获取token
        HashMap<String, String> header = getHeader();

        try {
            CloseableHttpResponse response = RestClient.get(url, header);
            JsonObject responseBody = TestUtil.getResponseBody(response);
            String expireTime = TestUtil.getValueByJPath(responseBody, "data");
            return expireTime;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    //new payment link....
    public CloseableHttpResponse createNewPaymentFromMC(String outletId, String orderAmount, String ecomOrderId) {

        //这个接口可以支持online 和offline的outlet创建paymentid
        String url = TestUtil.getHostName() + Path.CREATEPAYMENT;
        //获取token
        HashMap<String, String> header = getHeader();
        String currentExpireTime = getCurrentExpireTime(outletId);
        String currency = TestUtil.getCurrency();
        String entityString = "{\"outletId\":\"S21C00150028\",\"amount\":321,\"expirationTime\":1656715891230,\"ecomOrderId\":\"22222\",\"currency\":\"SGD\"}";
        //将string转为Json
        JsonObject entityJson = TestUtil.getJsonObjectByString(entityString);

        if(null == ecomOrderId || ecomOrderId.isEmpty()){
            String randomEcommId = TestUtil.getRandomEcommId();
            entityJson.addProperty("ecomOrderId", randomEcommId);
        }else {
            entityJson.addProperty("ecomOrderId", ecomOrderId);

        }

        entityJson.addProperty("amount", orderAmount);
        entityJson.addProperty("outletId", outletId);
        entityJson.addProperty("expirationTime", currentExpireTime);
        entityJson.addProperty("currency", currency);

        entityString = entityJson.toString();
        System.out.println("创建payment的入参是： " + entityString);
        try {
            CloseableHttpResponse response = RestClient.post(url, entityString, header);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    private String getRedirectUrlFromMC(JsonObject responseBody) {
//        JSONObject responseBody = TestUtil.getResponseBody(response);
        if (!"success".equalsIgnoreCase(TestUtil.getValueByJPath(responseBody, "code"))) {
            return null;
        }
        String redirectUrl = TestUtil.getValueByJPath(responseBody, "data/redirectUrl");
        return redirectUrl;
    }


    //用app支付的方法
    public JsonObject PPayOrder(String outletId, String orderAmount) {

        CloseableHttpResponse paymentFromMC = createPaymentFromMC(outletId, orderAmount);
        JsonObject paymentFromMCJson = TestUtil.getResponseBody(paymentFromMC);
        System.out.println("！！！！paymentFromMC的response： " + paymentFromMCJson);
        if (!"success".equalsIgnoreCase(TestUtil.getValueByJPath(paymentFromMCJson, "code"))) {
            System.out.println("生成payment失败～～～，请检查环境！！！");
            return null;
        }
        String redirectUrlFromMC = getRedirectUrlFromMC(paymentFromMCJson);
        System.out.println("redirectUrlFromMC: " + redirectUrlFromMC);


        //模拟手机扫描gateway生成的二维码进行支付
        String currency = TestUtil.getCurrency();
        CloseableHttpResponse response = appLogin.payOrder(redirectUrlFromMC);
        JsonObject responseBody = TestUtil.getResponseBody(response);
        if (response != null) {
            System.out.println("支付返回结果：" + responseBody);
            return responseBody;
        }

        return null;
    }

    //用app支付使用新的CreatePayment的方法
    public JsonObject NewPayOrder(String outletId, String orderAmount,String ecomOrderId) {

        CloseableHttpResponse paymentFromMC = createNewPaymentFromMC(outletId, orderAmount, ecomOrderId);
        JsonObject paymentFromMCJson = TestUtil.getResponseBody(paymentFromMC);
        System.out.println("！！！！New--paymentFromMC的response： " + paymentFromMCJson);
        if (!"success".equalsIgnoreCase(TestUtil.getValueByJPath(paymentFromMCJson, "code"))) {
            System.out.println("生成payment失败～～～，请检查环境！！！");
            return null;
        }
        String redirectUrlFromMC = getRedirectUrlFromMC(paymentFromMCJson);
        System.out.println("redirectUrlFromMC: " + redirectUrlFromMC);


        //模拟手机扫描gateway生成的二维码进行支付
        String currency = TestUtil.getCurrency();
        CloseableHttpResponse response = appLogin.payOrder(redirectUrlFromMC);
        JsonObject responseBody = TestUtil.getResponseBody(response);
        if (response != null) {
            System.out.println("支付返回结果：" + responseBody);
            return responseBody;
        }

        return null;
    }

    //用app支付使用新的CreatePayment的方法
    public JsonObject payOrderByPaymentLink(String redirectUrlFromMC) {

        //模拟手机扫描gateway生成的二维码进行支付
        String currency = TestUtil.getCurrency();
        CloseableHttpResponse response = appLogin.payOrder(redirectUrlFromMC);
        JsonObject responseBody = TestUtil.getResponseBody(response);
        if (response != null) {
            System.out.println("支付返回结果：" + responseBody);
            return responseBody;
        }

        return null;
    }
//    @Override
//    public CloseableHttpResponse payOrders(String paymentId) {
//        /**
//         * payOrders使用paymentId支付成功
//         * 创建payment成功后返回的是referenceId，需要将两者进行转化
//         */
//        return null;
//    }


    public void reverseOrder(String orderId, String port, JsonObject login) {
        String url = "http://localhost:" + port + "/orders/reverse";

        //红冲的订单和merchantid
        String entityString = "[\n" +
            "    {\n" +
            "        \"orderId\": \"O1097519858\",\n" +
            "        \"merchantId\": \"S21C00150002\",\n" +
            "        \"mdr\": 0,\n" +
            "        \"rebate\":0,\n" +
            "        \"institutionPartnerId\": null,\n" +
            "        \"profitSharingRate\": null,\n" +
            "        \"atomeFundedVoucherAmount\": null,\n" +
            "        \"merchantFundedVoucherAmount\": null\n" +
            "    }\n" +
            "]";
        HashMap<String, String> header = getHeader();

//        JSONObject entityJson = TestUtil.getJsonObjectByString(entityString);

        // 1、查询merchantid的MDR和Rebate，修改数据库
        // 2、查找符合条件的order，
        // 3、发起红冲
        try {
            RestClient.post(url, entityString, header);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getOnlineOutletIDToMerchant() {
        /*
        1、根据me接口获取当前companyid
        2、然后从数据库中通过companyid获取online的normal状态的to merchant的outlet
         */
        String companyId = getCompanyIdByMe();
        List<String> outletIdsLists = merchantMapper.searchOnlineAndNormalAndToMerchantOutletId(companyId);
        if (!outletIdsLists.isEmpty()) {
            System.out.println("#outletIdsLists.stream().findFirst() --- #获取到的to merchant -outletid是 ： " + outletIdsLists.stream().findFirst());
            return outletIdsLists.get(0);
        }
        return null;
    }


    public String getOnlineOutletIdToPsp() {
        /*
        1、根据me接口获取当前companyid
        2、然后从数据库中通过companyid获取online的normal状态的to psp的outlet
         */
        String companyId = getCompanyIdByMe();
        List<String> outletIds = merchantMapper.searchOnlineAndNormalAndToPspOutletId(companyId);
        if (!outletIds.isEmpty()) {
            System.out.println("获取到的to psp -outletid是 ： " + outletIds.get(0));
            return outletIds.get(0);
        }
        return null;
    }


    public CloseableHttpResponse getMe() {
        String url = TestUtil.getHostName() + Path.ME;
        HashMap<String, String> header = getHeader();

        try {
            CloseableHttpResponse response = RestClient.get(url, header);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    public CloseableHttpResponse getOutlets() {
        String url = TestUtil.getHostName() + Path.OUTLETS;
        HashMap<String, String> header = getHeader();

        try {
            CloseableHttpResponse response = RestClient.get(url, header);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    @Deprecated
    public CloseableHttpResponse getProfile() {
        String url = TestUtil.getHostName() + "/api/v2/profile";
        HashMap<String, String> header = getHeader();

        try {
            CloseableHttpResponse response = RestClient.get(url, header);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    public CloseableHttpResponse getPayoutList(String startTime, String endTime, String... outletIds) {

        HashMap<String, String> header = getHeader();
        String url = TestUtil.getHostName() + Path.PAYOUT;

        if (null == startTime || null == endTime || startTime.isEmpty() || endTime.isEmpty()) {
            url = url + "?limit=20&offset=0";
        } else {
            String strateTimeStamp = MyDateUtil.getStrateTimeStamp(startTime);
            String endTimeStamp = MyDateUtil.getEndTimeStamp(endTime);
            url = url + "?limit=20&offset=0&endTime=" + endTimeStamp + "&startTime=" + strateTimeStamp;
        }

        if (outletIds.length > 0) {
            String tmp = null;
            for (String outletId : outletIds) {
                tmp += "&outletIds=" + outletId;
            }
            url += tmp;
        }

        System.out.println("请求的接口参数： " + url);
        try {
            CloseableHttpResponse response = RestClient.get(url, header);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            CloseableHttpResponse response = RestClient.get(url, header);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    public String getCompanyIdByMe() {
        CloseableHttpResponse response = getMe();
        JsonObject responseBody = TestUtil.getResponseBody(response);
        String companyId = TestUtil.getValueByJPath(responseBody, "data/company/id");
        return companyId;
    }

    public CloseableHttpResponse refundFromMC(String refundAmount, String orderId) {
        //待优化，最好的方式是下单后进行退款
        String entityString;
        try {

            //获取退款入参
            entityString = TestUtil.getJsonStringFromFile(new File(System.getProperty("user.dir") + "/src/main/java/com/shanlu/data/refundForMC.json"));
            System.out.printf("MC退款原参数: " + entityString);
            //入参类型转换为json格式
//            JSONObject entityJson = JSONObject.parseObject(entityString);
            JsonObject entityJson = TestUtil.getJsonObjectByString(entityString);
            /*
            CloseableHttpResponse paymentFromMC = createPaymentFromMC(outletId);
            JsonObject paymentFromMCJson = TestUtil.getResponseBody(paymentFromMC);
            if(!"success".equalsIgnoreCase(TestUtil.getValueByJPath(paymentFromMCJson,"code"))){
                System.out.println("生成payment失败～～～，请检查环境！！！");
                return null;
            }

            String redirectUrlFromMC = getRedirectUrlFromMC(paymentFromMCJson);

            String currency = TestUtil.getCurrency();
            CloseableHttpResponse payOrderResultResponse = appLogin.payOrder(outletId,currency,redirectUrlFromMC);
            int payOrderStatusCode = TestUtil.getStatusCode(payOrderResultResponse);
            JsonObject payOrderResult = TestUtil.getResponseBody(payOrderResultResponse);
            System.out.println("支付结果： " + payOrderResult);
            String code = TestUtil.getValueByJPath(payOrderResult, "code");
            if(!"success".equalsIgnoreCase(code)){
                System.out.println("支付失败，请检查参数～～～" );
                throw new RuntimeException("支付失败： "+payOrderResult);
            }
            */

            //此处线程等待2s，数据保存入库
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //根据orderId获取transactionId
            String transactionId = merchantMapper.getTransactionIdByOrderId(orderId);
            System.out.println("从数据库中获取到的TransactionId 为： "+transactionId);

            if(null == transactionId && ""==transactionId){
                System.out.println("获取的TransactionId 失败！！！");
                return null;
            }

            if (orderId.isEmpty()) return null;
            double refund_Amount = Double.parseDouble(refundAmount);

            if (refund_Amount <= 0) {
                System.out.println("退款金额不能小于等于0");
                return null;
            }

            /*
            //获取入参中的transactionId
            String transactionId = TestUtil.getValueByJPath(entityJson, "transactionId");
            */

            //根据transactionId获取transactionDetail
            CloseableHttpResponse transactionDetail = getTransactionDetail(transactionId);

            //获取transactionDetail中的最大可退款金额
            Object maxRefundAmountRes = getMaxRefundAmountByTransactionDetail(transactionDetail);
            if(null == maxRefundAmountRes) return null;
            Double maxRefundAmount = (Double) maxRefundAmountRes;
            if (maxRefundAmount > 0 && refund_Amount <= maxRefundAmount) {
                //拼装入参

                entityJson.addProperty("orderId", orderId);
                entityJson.addProperty("transactionId", transactionId);
                entityJson.addProperty("refundAmount", refundAmount);
                entityJson.addProperty("maxRefundAmount", maxRefundAmount);
            } else {
                System.out.println("最大可退款金额为0，不支持退款！### " + maxRefundAmount);
                return null;
            }

            //发起退款
            String url = TestUtil.getHostName() + Path.REFUND;
            //入参数据格式化
            entityString = TestUtil.formatParam(entityJson);

            System.out.println("MC退款实际请求参数是： " + entityString);

            HashMap<String, String> header = getHeader();

            CloseableHttpResponse response = RestClient.post(url, entityString, header);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        YamlReader.COUNTRY = "SG";
        McLogin mcLogin = new McLogin(YamlReader.COUNTRY);
        String entityString = "{\"username\":\"company_lushan\", \"password\":\"changeMe123!\"}";
        JsonObject login = mcLogin.login();
        System.out.println(login);
    }
}
