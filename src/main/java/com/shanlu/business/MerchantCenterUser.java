package com.shanlu.business;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanlu.common.*;
import com.shanlu.util.RestClient;
import com.shanlu.util.TestUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

public class MerchantCenterUser {
    private static final String LOGIN_URL = "/api/login";
    private static final String CREATE_PAYMENT_URL = "/api/v2/transactions/create";
    private static final String TRANSACTION_DETAIL= "/api/v2/transaction";

    private final CloseableHttpClient client;
    private final ObjectMapper objectMapper;
    private final LoginParam loginParam;
    private final String hostname;
    private String token;

    public MerchantCenterUser(CloseableHttpClient client, ObjectMapper objectMapper, String username, String password, String hostname) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.loginParam = new LoginParam(username, password);
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
        JavaType type = objectMapper.getTypeFactory().constructParametricType(ResponseBody.class, MerchantCenterLoginRes.class);
        ResponseBody<MerchantCenterLoginRes> merchantCenterLoginRes = objectMapper.readValue(response.getEntity().getContent(), type);
        this.token = merchantCenterLoginRes.getData().getToken();
    }

    public String getToken() throws IOException {
        if (this.token != null) {
            return this.token;
        }
        login();
        return this.token;
    }

    public URL createPaymentUrl(CreatePaymentUrlParam param) throws IOException {
        getToken();
        HttpPost httpPost = new HttpPost(hostname + CREATE_PAYMENT_URL);
        String createPaymentParamString = objectMapper.writeValueAsString(param);
        StringEntity createPaymentParamBody = new StringEntity(createPaymentParamString);
        httpPost.setEntity(createPaymentParamBody);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("cookie", "Authorization: " + this.token);

        CloseableHttpResponse response = client.execute(httpPost);
        JavaType type = objectMapper.getTypeFactory().constructParametricType(ResponseBody.class, CreatePaymentUrlRes.class);
        ResponseBody<CreatePaymentUrlRes> createPaymentUrlRes = objectMapper.readValue(response.getEntity().getContent(), type);
        return new URL(createPaymentUrlRes.getData().getRedirectUrl());
    }

    private static class LoginParam {
        String username;
        String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public LoginParam(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public TransactionDetailRes getTransactionDetail(String transactionId) throws IOException, URISyntaxException {
        getToken();

        URI uri = new URIBuilder(hostname + TRANSACTION_DETAIL)
                .addParameter("transactionId", transactionId)
                .build();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("cookie", "Authorization: " + this.token);

        CloseableHttpResponse response = client.execute(httpGet);
        JavaType type = objectMapper.getTypeFactory().constructParametricType(ResponseBody.class, TransactionDetailRes.class);
        ResponseBody<TransactionDetailRes> transactionDetailRes = objectMapper.readValue(response.getEntity().getContent(), type);

        return transactionDetailRes.getData();

    }
}
