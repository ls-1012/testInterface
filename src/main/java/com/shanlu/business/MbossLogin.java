package com.shanlu.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shanlu.config.Path;
import com.shanlu.util.JsonUtil;
import com.shanlu.util.RestClient;
import com.shanlu.util.TestUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MbossLogin {
    String cookie;
    String currency;

    public MbossLogin(String cookie,String currency){
        this.cookie = cookie;
        this.currency = currency;
    }

    //拼接mboss的header
    public static HashMap<String,String> getMbossHeader(String cookie){
        HashMap<String,String> header = new HashMap<>();
        header.put("cookie",cookie);
        header.put("Content-Type", "application/json");

        return header;
    }


    public Pair<String,String> getAccessKeyAndPAsswordByMerchant(String cookie, String outlet){
//        Pair<String,String> pair = new MutablePair<>();
        CloseableHttpResponse merchantInfo = getMerchantInfoByOutletId(cookie, outlet);

        if(null!=merchantInfo){
            JsonObject responseJson = TestUtil.getResponseBody(merchantInfo);
            System.out.println("从mboss获取到的merchantInfo信息： " + responseJson);
            String accesskey = TestUtil.getValueByJPath(responseJson, "data/basic/paymentSecrets[0]/accessKey");
            String password = TestUtil.getValueByJPath(responseJson, "data/basic/paymentSecrets[0]/password");
            System.out.println("accesskey: "+accesskey+",password: "+ password);
            return Pair.of(accesskey, password);
        }

        return null;
    }


    public CloseableHttpResponse getMerchantInfoByOutletId(String cookie,String outlet){
        HashMap<String, String> header = getMbossHeader(cookie);

        String url = TestUtil.getMbossHostName()+"/api/merchants/info/"+outlet;
        try {
            CloseableHttpResponse response = RestClient.get(url, header);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JsonObject setCompanyParam(String companyRegistrationName,String companyRegistrationNumber,String param){
        JsonObject paramJson = TestUtil.formatParam(param);
        System.out.println("create company param: "+param);
        //设置basic
        JsonObject basic = paramJson.get("basic").getAsJsonObject();
        if(companyRegistrationName.isEmpty() && companyRegistrationNumber.isEmpty()){
            String randomComRegNumberId = TestUtil.getRandomComRegNumberId();
            basic.addProperty("companyRegistrationName",randomComRegNumberId);
            basic.addProperty("companyRegistrationNumber",randomComRegNumberId);
        }else if(companyRegistrationName.isEmpty()){
            basic.addProperty("companyRegistrationName",companyRegistrationNumber);
            basic.addProperty("companyRegistrationNumber",companyRegistrationNumber);
        }else if(companyRegistrationNumber.isEmpty()){
            basic.addProperty("companyRegistrationName",companyRegistrationName);
            basic.addProperty("companyRegistrationNumber",companyRegistrationName);
        }else{
            basic.addProperty("companyRegistrationName",companyRegistrationName);
            basic.addProperty("companyRegistrationNumber",companyRegistrationNumber);
        }
        //设置currency
        JsonObject commercial = paramJson.get("commercial").getAsJsonObject();
        commercial.addProperty("currency",TestUtil.getCurrency());

        //设置account
        JsonObject account = paramJson.get("account").getAsJsonObject();
        JsonArray usernameList = account.get("usernameList").getAsJsonArray();
//            System.out.println(usernameList);
//            System.out.println(usernameList.size());
        if(usernameList.size()!=0){
            for (int i=usernameList.size();i>0;i--) {
                usernameList.remove(i-1);
            }
        }
        usernameList.add(paramJson.get("basic").getAsJsonObject().get("companyRegistrationNumber"));
        return paramJson;
    }

    private JsonObject setOutletParam(String companyId, String merchantName,String displayName,String param,String merchantType){
        JsonObject paramJson = TestUtil.formatParam(param);
//        System.out.println("create outlet param: "+param);
        if(companyId.isEmpty() || merchantType.isEmpty() || param.isEmpty() ) return null;


        //设置basic
        JsonObject basic = paramJson.get("basic").getAsJsonObject();
        if(merchantType == "Online"){
            String merchantBrandId = createMerchantBrand();

            if(merchantBrandId.isEmpty()) {
                System.out.println("创建merchant brand 失败～～～" );
                return null;
            }
            basic.addProperty("merchantBrandId",merchantBrandId);
        }

        basic.addProperty("belongingsTo",companyId);
        basic.addProperty("companyId",companyId);
        if(merchantName.isEmpty() && displayName.isEmpty()){
            String randomComRegNumberId = TestUtil.getRandomComRegNumberId();
            basic.addProperty("merchantName",randomComRegNumberId);
            basic.addProperty("displayName",randomComRegNumberId);
        }else if(merchantName.isEmpty()){
            basic.addProperty("merchantName",displayName);
            basic.addProperty("displayName",displayName);
        }else if(displayName.isEmpty()){
            basic.addProperty("merchantName",merchantName);
            basic.addProperty("displayName",merchantName);
        }else{
            basic.addProperty("merchantName",merchantName);
            basic.addProperty("displayName",displayName);
        }

        //设置account
        JsonObject account = paramJson.get("account").getAsJsonObject();
        JsonArray usernameList = account.get("usernameList").getAsJsonArray();
//            System.out.println(usernameList);
//            System.out.println(usernameList.size());
        if(usernameList.size()!=0){
            for (int i=usernameList.size();i>0;i--) {
                usernameList.remove(i-1);
            }
        }
        usernameList.add(paramJson.get("basic").getAsJsonObject().get("merchantName"));

        System.out.println("create outlet paramJson: "+paramJson);

        return paramJson;
    }

    public String  createNormalCompany(String companyRegistrationName,String companyRegistrationNumber){
        /**
         * 创建company，不允许创建重复的companyRegistrationName和companyRegistrationNumber，usernameList 此处设置为同companyRegistrationNumber
         * currency 根据不同的国家线使用不同的值
         * 因此，这几个值，可以作为参数传进来，也可以自动生成。
         *
         * {
         *   "basic": {
         *     "companyRegistrationName": "shanlu_test1",
         *     "companyRegistrationNumber": "shanlu_test1",
         *     "registeredOfficeState": "中国",
         *     "registeredOfficeCity": "北京",
         *     "registeredOfficeDistrict": "北京市",
         *     "registeredOfficeAddress": "西城牛街",
         *     "postCode": "123456",
         *     "contactPerson1": "shan lu",
         *     "phoneNumber": "13800012393",
         *     "riskRating": "BLANK",
         *     "email": "shan.lu@adcance.ai",
         *     "pic": "3332323",
         *     "contractDate": 1657250881743
         *   },
         *   "bank": {
         *     "accountNumber": ""
         *   },
         *   "commercial": {
         *     "currency": "SGD"
         *   },
         *   "other": {},
         *   "account": {
         *     "usernameList": [
         *       "shanlu_test1"
         *     ],
         *     "bd": "Admin"
         *   },
         *   "japanReportInfo": null
         * }
         */



        String url = TestUtil.getMbossHostName()+ Path.SUBMITCREATECOMPANY;
        HashMap<String, String> header = getMbossHeader(cookie);

        try {

            String entityString = TestUtil.getJsonStringFromFile(new File(System.getProperty("user.dir") + "/src/main/java/com/shanlu/data/createCompany.json"));
            if (!JsonUtil.verifyCurrency(entityString, currency)) return null;
            String param = JsonUtil.getInputParamByCurrency(entityString, currency);

            JsonObject paramJson = setCompanyParam(companyRegistrationName, companyRegistrationNumber, param);

            System.out.println("请求 create company的参数为： "+ paramJson);

            String paramString = TestUtil.formatParam(paramJson);

            CloseableHttpResponse createCompanyRes = RestClient.post(url, paramString,header);

            int statusCode = TestUtil.getStatusCode(createCompanyRes);
            System.out.println(statusCode);
            if(null != createCompanyRes) {
                System.out.println(createCompanyRes);
                JsonObject createCompanyJson = TestUtil.getResponseBody(createCompanyRes);
                System.out.println(createCompanyJson);
                String companyId= TestUtil.getValueByJPath(createCompanyJson, "data/companyId");
                return companyId;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String  createSavedCompany(String companyRegistrationName,String companyRegistrationNumber) {
        /**
         *  创建company，不允许创建重复的companyRegistrationName和companyRegistrationNumber，usernameList 此处设置为同companyRegistrationNumber
         *  因此，这几个值，可以作为参数传进来，也可以自动生成。
         *  currency 根据不同的国家线使用不同的值
         *
         *  参数是createCompany.json
         */
        String url = TestUtil.getMbossHostName()+ Path.SAVECREATECOMPANY;
        HashMap<String, String> header = getMbossHeader(cookie);

        try {

            String entityString = TestUtil.getJsonStringFromFile(new File(System.getProperty("user.dir") + "/src/main/java/com/shanlu/data/createCompany.json"));
            if (!JsonUtil.verifyCurrency(entityString, currency)) return null;
            String param = JsonUtil.getInputParamByCurrency(entityString, currency);

            JsonObject paramJson = setCompanyParam(companyRegistrationName, companyRegistrationNumber, param);

            System.out.println("请求 create company的参数为： "+ paramJson);

            String paramString = TestUtil.formatParam(paramJson);

            CloseableHttpResponse createCompanyRes = RestClient.post(url, paramString,header);

            int statusCode = TestUtil.getStatusCode(createCompanyRes);
            System.out.println(statusCode);
            if(null != createCompanyRes) {
                JsonObject createCompanyJson = TestUtil.getResponseBody(createCompanyRes);
                String companyId= TestUtil.getValueByJPath(createCompanyJson, "data/companyId");
                return companyId;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String  createOfflineSaveOutlet(String companyId, String merchantName,String displayName){
        /**
         * 1、outlet创建分为online和offline
         * 2、每种type都存在两种情况：
         *    1）从0直接点击submit，，创建outlet----enable(UAT)
         *    2）从0直接点击save，创建outlet----saved
         *    3) 从saved状态点击submit，创建outlet----enabled
         *    4) 从saved状态点击duplicate，创建new outlet----saved
         *
         * 说明：创建offline outlet时，需要动态改变的参数有：companyId, merchantName，displayName，usernameList
         *
         */
        String url = TestUtil.getMbossHostName()+ Path.SAVECREATEOUTLET;
        HashMap<String, String> header = getMbossHeader(cookie);

        String entityString = null;
        try {
            entityString = TestUtil.getJsonStringFromFile(new File(System.getProperty("user.dir") + "/src/main/java/com/shanlu/data/createOfflineOutlet.json"));
            if (!JsonUtil.verifyCurrency(entityString, currency)) return null;
            String param = JsonUtil.getInputParamByCurrency(entityString, currency);

            JsonObject paramJson = setOutletParam(companyId,merchantName, displayName, param,"Offline");

            System.out.println("请求 offline outlet的参数为： "+ paramJson);

            String paramString = TestUtil.formatParam(paramJson);
            CloseableHttpResponse createOutletRes = RestClient.post(url, paramString,header);
            int statusCode = TestUtil.getStatusCode(createOutletRes);
            System.out.println(statusCode);
            if(null != createOutletRes) {
                JsonObject createCompanyJson = TestUtil.getResponseBody(createOutletRes);
                String merchantId= TestUtil.getValueByJPath(createCompanyJson, "data/merchantId");
                return merchantId;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return "";
    }

    public String  createOfflineEnableOutlet(String companyId, String merchantName,String displayName) {
        String url = TestUtil.getMbossHostName()+ Path.SUBMITCREATEOUTLET;
        HashMap<String, String> header = getMbossHeader(cookie);

        String entityString = null;
        try {
            entityString = TestUtil.getJsonStringFromFile(new File(System.getProperty("user.dir") + "/src/main/java/com/shanlu/data/createOfflineOutlet.json"));
            if (!JsonUtil.verifyCurrency(entityString, currency)) return null;
            String param = JsonUtil.getInputParamByCurrency(entityString, currency);

            JsonObject paramJson = setOutletParam(companyId,merchantName, displayName, param,"Offline");

            System.out.println("请求 offline outlet的参数为： "+ paramJson);

            String paramString = TestUtil.formatParam(paramJson);
            CloseableHttpResponse createOutletRes = RestClient.post(url, paramString,header);
            int statusCode = TestUtil.getStatusCode(createOutletRes);
            System.out.println(statusCode);
            if(null != createOutletRes) {
                JsonObject createCompanyJson = TestUtil.getResponseBody(createOutletRes);
                String merchantId= TestUtil.getValueByJPath(createCompanyJson, "data/merchantId");
                return merchantId;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return "";
    }

    public String  createOnlineSaveOutlet(String companyId, String merchantName,String displayName){
        /**
         * 创建 online outlet时，需要动态改变的参数有：companyId, belongingsTo, merchantName，displayName，usernameList, merchantBrandId
         */
        String url = TestUtil.getMbossHostName()+ Path.SAVECREATEOUTLET;
        System.out.println("url: "+url);
        HashMap<String, String> header = getMbossHeader(cookie);

        String entityString = null;
        try {
            entityString = TestUtil.getJsonStringFromFile(new File(System.getProperty("user.dir") + "/src/main/java/com/shanlu/data/createOnlineOutlet.json"));
            if (!JsonUtil.verifyCurrency(entityString, currency)) return null;
            String param = JsonUtil.getInputParamByCurrency(entityString, currency);
            System.out.println("param: "+param);
            JsonObject paramJson = setOutletParam(companyId,merchantName, displayName, param,"Online");

            System.out.println("请求 online outlet的参数为： "+ paramJson);

            String paramString = TestUtil.formatParam(paramJson);
            CloseableHttpResponse createOutletRes = RestClient.post(url, paramString,header);
//            int statusCode = TestUtil.getStatusCode(createOutletRes);
//            System.out.println(statusCode);
            if(null != createOutletRes) {
                JsonObject createCompanyJson = TestUtil.getResponseBody(createOutletRes);
                String merchantId= TestUtil.getValueByJPath(createCompanyJson, "data/merchantId");
                return merchantId;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return "";
    }

    public String  createOnlineEnableOutlet(String companyId, String merchantName,String displayName){
        /**
         * 创建 online outlet时，需要动态改变的参数有：companyId, belongingsTo, merchantName，displayName，usernameList, merchantBrandId
         */
        String url = TestUtil.getMbossHostName()+ Path.SUBMITCREATEOUTLET;
        HashMap<String, String> header = getMbossHeader(cookie);

        String entityString = null;
        try {
            entityString = TestUtil.getJsonStringFromFile(new File(System.getProperty("user.dir") + "/src/main/java/com/shanlu/data/createOnlineOutlet.json"));
            if (!JsonUtil.verifyCurrency(entityString, currency)) return null;
            String param = JsonUtil.getInputParamByCurrency(entityString, currency);

            JsonObject paramJson = setOutletParam(companyId,merchantName, displayName, param,"Online");

            System.out.println("请求 online outlet的参数为： "+ paramJson);

            String paramString = TestUtil.formatParam(paramJson);
            CloseableHttpResponse createOutletRes = RestClient.post(url, paramString,header);
            int statusCode = TestUtil.getStatusCode(createOutletRes);
            System.out.println(statusCode);
            if(null != createOutletRes) {
                JsonObject createCompanyJson = TestUtil.getResponseBody(createOutletRes);
                String merchantId= TestUtil.getValueByJPath(createCompanyJson, "data/merchantId");
                return merchantId;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return "";
    }

    public String createMerchantBrand(){

        String url = TestUtil.getMbossHostName()+ Path.CREATEBRAND;
        HashMap<String, String> header = getMbossHeader(cookie);

        String entityString = null;
        try {
            entityString = "{\n" +
                "    \"merchantBrandName\": \"predo\",\n" +
                "    \"merchantBrandBrandName\": \"predo\",\n" +
                "    \"merchantBrandTier\": \"A\"\n" +
                "}";
//            if (!JsonUtil.verifyCurrency(entityString, currency)) return null;
//            String param = JsonUtil.getInputParamByCurrency(entityString, currency);

            JsonObject paramJson = TestUtil.formatParam(entityString);
            String randomMerBrandId = TestUtil.getRandomMerBrandId();
            paramJson.addProperty("merchantBrandName",randomMerBrandId);
            paramJson.addProperty("merchantBrandBrandName",randomMerBrandId);
            String paramString = TestUtil.formatParam(paramJson);

            CloseableHttpResponse createBrandRes = RestClient.post(url, paramString,header);
            int statusCode = TestUtil.getStatusCode(createBrandRes);
            System.out.println(statusCode);
            if(null != createBrandRes) {
                JsonObject createBrandJson = TestUtil.getResponseBody(createBrandRes);
                String merchantBrandId= TestUtil.getValueByJPath(createBrandJson, "data/merchantBrandId");
                return merchantBrandId;
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return "";

    }

}
