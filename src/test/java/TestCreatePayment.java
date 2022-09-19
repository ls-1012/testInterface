
import com.google.gson.JsonObject;
import com.shanlu.business.McLogin;
import com.shanlu.business.OpenApi;
import com.shanlu.util.TestUtil;
import com.shanlu.util.YamlReader;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import static com.shanlu.base.TestBase.RESPONSE_STATUS_CODE_200;


public class TestCreatePayment {




    @Test
    public void testCreatePaymentFromMC() {
        /**
         * 可以生成to psp的online的支付二维码
         * 也可以生成to merchant的online的支付二维码
         */

        YamlReader.COUNTRY = "SG";
        if(YamlReader.verifyCurrency(YamlReader.COUNTRY)){
            McLogin mcLogin = new McLogin(YamlReader.COUNTRY);
//            OpenApi openApi = new OpenApi(YamlReader.COUNTRY);
//            String outletId = mcLogin.getOnlineOutletIDToMerchant();
            String outletId = "S21C00150016";

            CloseableHttpResponse response = mcLogin.createPaymentFromMC(outletId,"3.1");
            JsonObject responseBody = TestUtil.getResponseBody(response);
            System.out.println("生成的payment URL是： " + responseBody);
            int statusCode = TestUtil.getStatusCode(response);
            Assertions.assertEquals(statusCode, RESPONSE_STATUS_CODE_200);

        }
    }

    @Test
    public void testNewCreatePaymentFromMC() {
        /**
         * 可以生成to psp的online的支付二维码
         * 也可以生成to merchant的online的支付二维码
         */

        YamlReader.COUNTRY = "SG";
        if(YamlReader.verifyCurrency(YamlReader.COUNTRY)){
            McLogin mcLogin = new McLogin(YamlReader.COUNTRY);
//            OpenApi openApi = new OpenApi(YamlReader.COUNTRY);
//            String outletId = mcLogin.getOnlineOutletIDToMerchant();
            String outletId = "S22G00270008";

            CloseableHttpResponse response = mcLogin.createNewPaymentFromMC(outletId,"3.1","");
            JsonObject responseBody = TestUtil.getResponseBody(response);
            System.out.println("生成的payment URL是： " + responseBody);
            int statusCode = TestUtil.getStatusCode(response);
            Assertions.assertEquals(statusCode, RESPONSE_STATUS_CODE_200);

        }
    }

    @Test
    public void testCreatePaymentViaOpenAPI(){
        /**
         * 手动获取mboss 的token
         */

        YamlReader.COUNTRY = "SG";
        if(YamlReader.verifyCurrency(YamlReader.COUNTRY)) {
            String cookie = "xxxxx";
            McLogin mcLogin = new McLogin(YamlReader.COUNTRY);
            OpenApi openApi = new OpenApi(YamlReader.COUNTRY,cookie);
//            String outletId = mcLogin.getOnlineOutletIDToMerchant();
            String outletId = "S21C00150016";

            CloseableHttpResponse response = openApi.createPaymentViaOpenAPI(cookie, outletId,"1000");
            JsonObject responseBody = TestUtil.getResponseBody(response);
            System.out.println("openapi 创建的payment url： " + responseBody);
//            int statusCode = TestUtil.getStatusCode(response);
        Assertions.assertEquals(responseBody.get("status").getAsString(), "PROCESSING");

        }
    }
}
