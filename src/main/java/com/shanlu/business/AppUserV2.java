package com.shanlu.business;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanlu.common.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class AppUserV2 {
    private static final String LOGIN_URL = "/api/login";
    private static final String PUBLIC_PAYMENT_URL = "https://sg-gateway.apaylater.net/api/payment/public";
    private static final String PAYMENT_METHODS_URL = "/api/payment-methods";
    private static final String MAKE_PAYMENT_URL = "/api/orders";
    private static final String MAKE_PAYMENT_PLANS_URL = "/api/order-plans";

    private final CloseableHttpClient client;
    private final ObjectMapper objectMapper;
    private final LoginParam loginParam;
    private final String hostname;
    private String token;
    private Pair<String, String> paymentInstrumentIdPaymentTypePair;

    public AppUserV2(CloseableHttpClient client, ObjectMapper objectMapper, String mobileNumber, String otp, String hostname) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.loginParam = new LoginParam(mobileNumber, otp);
        this.hostname = hostname;
    }

    private void login() throws IOException {
        HttpPost httpPost = new HttpPost(hostname + LOGIN_URL);
        String loginParamString = objectMapper.writeValueAsString(this.loginParam);
        StringEntity loginParamBody = new StringEntity(loginParamString);
        httpPost.setEntity(loginParamBody);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);
        JavaType type = objectMapper.getTypeFactory().constructParametricType(ResponseBody.class, AppLoginRes.class);
        ResponseBody<AppLoginRes> merchantCenterLoginRes = objectMapper.readValue(response.getEntity().getContent(), type);
        this.token = merchantCenterLoginRes.getData().getJwt();
    }

    public String getToken() throws IOException {
        if (this.token != null) {
            return this.token;
        }
        login();
        return this.token;
    }

    public Pair<String, String> getFirstPaymentMethod() throws IOException {
        if (this.paymentInstrumentIdPaymentTypePair != null) {
            return this.paymentInstrumentIdPaymentTypePair;
        }

        HttpGet httpGet = new HttpGet(hostname + PAYMENT_METHODS_URL);
        httpGet.setHeader("X-ADV-TOKEN", getToken());

        CloseableHttpResponse response = client.execute(httpGet);
        JavaType type = objectMapper.getTypeFactory().constructParametricType(ResponseBody.class, PaymentMethodRes.class);
        ResponseBody<PaymentMethodRes> merchantCenterLoginRes = objectMapper.readValue(response.getEntity().getContent(), type);
        PaymentMethodRes paymentMethodRes = merchantCenterLoginRes.getData();
        return Pair.of(paymentMethodRes.getFirstPaymentInstrumentId(), paymentMethodRes.getFirstPaymentMethodType());
    }

    public void makePayment(URL paymentURL) throws URISyntaxException, IOException {
        String paymentToken = paymentURL.getQuery().replace("token=", "");
        MakePaymentParam makePaymentParam = buildMakePaymentParam(paymentToken);
        makeOrderPlans(makePaymentParam.getPaymentId());
        HttpPost httpPost = new HttpPost(hostname + MAKE_PAYMENT_URL);
        String paramString = objectMapper.writeValueAsString(makePaymentParam);
        StringEntity paramBody = new StringEntity(paramString);
        httpPost.setEntity(paramBody);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("cookie", "X-ADV-TOKEN=" + getToken());
        httpPost.setHeader("x-user-agent", "Apaylater (iOS; iPhone 8; 15.1; zh-Hans-SG) uuid/579d6678-c1ea-4829-8664-b80750322a2d version/3.17.0+450");
        httpPost.setHeader("device-id", "579d6678-c1ea-4829-8664-b80750322a2d");

        CloseableHttpResponse response = client.execute(httpPost);
//        JavaType type = objectMapper.getTypeFactory().constructParametricType(ResponseBody.class, AppLoginRes.class);
        JsonNode res = objectMapper.readValue(response.getEntity().getContent(), JsonNode.class);
        System.out.println(res.toString());
//        this.token = merchantCenterLoginRes.getData().getJwt();
    }

    private MakePaymentParam buildMakePaymentParam(String paymentToken) throws URISyntaxException, IOException {
        PublicPaymentRes publicPaymentInfo = getPublicPaymentInfo(paymentToken);
        String paymentId = publicPaymentInfo.getPaymentId();
        String productId = publicPaymentInfo.getFirstProductId();
        Pair<String, String> paymentInstrumentIdPaymentTypePairRes = getFirstPaymentMethod();
        String paymentInstrumentId = paymentInstrumentIdPaymentTypePairRes.getLeft();
        String paymentType = paymentInstrumentIdPaymentTypePairRes.getRight();

        MakePaymentParam makePaymentParam = new MakePaymentParam();
        makePaymentParam.setPaymentId(paymentId);
        makePaymentParam.setProductId(productId);
        makePaymentParam.setCardId(paymentInstrumentId);
        makePaymentParam.setPaymentMethodType(paymentType);
        return makePaymentParam;
    }

    private void makeOrderPlans(String paymentId) throws URISyntaxException, IOException {
        URI uri = new URIBuilder(hostname + MAKE_PAYMENT_PLANS_URL)
                .addParameter("paymentId", paymentId)
                .addParameter("appActionSource", "QRCODE")
                .addParameter("atomePlusPoints", "0")
                .build();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader("cookie", "X-ADV-TOKEN=" + getToken());
        CloseableHttpResponse response = client.execute(httpGet);
        JavaType type = objectMapper.getTypeFactory().constructParametricType(ResponseBody.class, Object.class);
        ResponseBody<Object> res = objectMapper.readValue(response.getEntity().getContent(), type);
        if (response.getStatusLine().getStatusCode() != 200 || !res.isSuccess()) {
            throw new ConnectException("Failed to make order plans");
        }
    }

    private PublicPaymentRes getPublicPaymentInfo(String paymentToken) throws URISyntaxException, IOException {
        HttpGet httpGet = new HttpGet(PUBLIC_PAYMENT_URL);
        URI uri = new URIBuilder(httpGet.getURI())
                .addParameter("token", paymentToken)
                .build();
        httpGet.setURI(uri);

        CloseableHttpResponse response = client.execute(httpGet);
        JavaType type = objectMapper.getTypeFactory().constructParametricType(ResponseBody.class, PublicPaymentRes.class);
        ResponseBody<PublicPaymentRes> merchantCenterLoginRes = objectMapper.readValue(response.getEntity().getContent(), type);
        return merchantCenterLoginRes.getData();
    }

    private static class LoginParam {
        String mobileNumber;
        String otp;

        public LoginParam(String mobileNumber, String otp) {
            this.mobileNumber = mobileNumber;
            this.otp = otp;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }
    }
}
