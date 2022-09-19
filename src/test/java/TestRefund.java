import com.google.gson.JsonObject;
import com.shanlu.business.McLogin;
import com.shanlu.business.OpenApi;
import com.shanlu.config.BaseDAO;
import com.shanlu.mapper.MerchantMapper;
import com.shanlu.util.TestUtil;
import com.shanlu.util.YamlReader;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestRefund {
    @BeforeAll
    public static void executeShell(){
        System.out.println("执行 /Users/shanlu/script/port.sh sg... ");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process exec = Runtime.getRuntime().exec("/Users/shanlu/script/port.sh sg");
                    int i = 0;
                    try {
                        i = exec.waitFor();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("返回结果： "+i);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
        System.out.println("before 执行结束。。。。");

    }

    @Test
    public void testRefundByNewOrder(){
        YamlReader.COUNTRY = "SG";

        if (YamlReader.verifyCurrency(YamlReader.COUNTRY)) {

            McLogin mcLogin = new McLogin(YamlReader.COUNTRY);
            String outletId = "S21C00150016";

            JsonObject payOrderResult = mcLogin.PPayOrder(outletId,"37.37");
            System.out.println(payOrderResult);

            String orderId = TestUtil.getValueByJPath(payOrderResult, "data/id");
            System.out.println("orderId为："+orderId);

            CloseableHttpResponse response = mcLogin.refundFromMC("3.38", orderId);

            JsonObject refundJson = TestUtil.getResponseBody(response);
            System.out.println("refund 响应结果为：" + refundJson);
        }

    }

    @Test
    public void testRefundByOldOrder() {
        YamlReader.COUNTRY = "SG";
        String orderId = "O1334717542";
        if (YamlReader.verifyCurrency(YamlReader.COUNTRY)) {

            McLogin mcLogin = new McLogin(YamlReader.COUNTRY);

            CloseableHttpResponse response = mcLogin.refundFromMC("1", orderId);

            JsonObject refundJson = TestUtil.getResponseBody(response);
            System.out.println("refund 响应结果为：" + refundJson);
        }
    }

    @Test
    public void testOpenAPIRefundByNewOrder(){
        YamlReader.COUNTRY = "SG";

        if (YamlReader.verifyCurrency(YamlReader.COUNTRY)) {
            String cookie = "xxxxx";

            OpenApi openApi = new OpenApi(YamlReader.COUNTRY,cookie);
            String outletId = "S21C00150016";

            JsonObject payOrderResult = openApi.PPayOrder(cookie,outletId,"1234");
            System.out.println(payOrderResult);

            String orderId = TestUtil.getValueByJPath(payOrderResult, "data/id");
            System.out.println("orderId为："+orderId);
            String paymentId = TestUtil.getValueByJPath(payOrderResult, "data/paymentId");
            System.out.println("paymentId为："+paymentId);

            CloseableHttpResponse response = openApi.refundViaAPI(cookie,outletId,"554" );

            JsonObject refundJson = TestUtil.getResponseBody(response);
            System.out.println("refund 响应结果为：" + refundJson);
        }

    }

    @Test
    public void testOpenAPIRefundByOldOrder() {
        YamlReader.COUNTRY = "SG";
//        String orderId = "O1338924820";
        String paymentId = "xxxxxxxx";
        if (YamlReader.verifyCurrency(YamlReader.COUNTRY)) {
            String cookie = "xxxxx";
            OpenApi openApi = new OpenApi(YamlReader.COUNTRY,cookie);

            String outletId = "S21C00150016";
            String referenceId = openApi.getReferenceIdByPaymentId(paymentId);
            System.out.println(referenceId);
            CloseableHttpResponse response = openApi.refundViaAPI(cookie,outletId,"554" );

            JsonObject refundJson = TestUtil.getResponseBody(response);
            System.out.println("refund 响应结果为：" + refundJson);
        }
    }

    @AfterAll
    public static void closeResource(){
        System.out.println("关闭资源");
        BaseDAO.close();
        System.out.println("关闭成功");
    }
}
