import com.google.gson.JsonObject;
import com.shanlu.business.McLogin;
import com.shanlu.business.OpenApi;
import com.shanlu.util.TestUtil;
import com.shanlu.util.YamlReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

public class TestPayOrder {


    @Test
    public void testSGPaySuccessByMC() {

        YamlReader.COUNTRY = "SG";

        String outletId = "S21C00150002";
        for (int i = 0; i < 1; i++) {
            McLogin mcLogin = new McLogin(YamlReader.COUNTRY);
            int dollar = ThreadLocalRandom.current().nextInt(20, 151);
            int cent = ThreadLocalRandom.current().nextInt(0, 100);
            if (YamlReader.verifyCurrency(YamlReader.COUNTRY)) {

                JsonObject jsonObject = mcLogin.PPayOrder(outletId, dollar + "." + cent);
//                System.out.println(jsonObject);
                String orderId = TestUtil.getValueByJPath(jsonObject, "data/id");
//                System.out.println("orderId为：" + orderId);
                Assertions.assertNotNull(orderId);

            }
        }
    }

    @Test
    public void testPaySuccessByPaymentLink() {

        YamlReader.COUNTRY = "TW";

        if (YamlReader.verifyCurrency(YamlReader.COUNTRY)) {

            McLogin mcLogin = new McLogin(YamlReader.COUNTRY);

            String outletId = "T22E00070005";
            String url = "xxx";

            JsonObject jsonObject = mcLogin.payOrderByPaymentLink(url);
            System.out.println(jsonObject);
            String orderId = TestUtil.getValueByJPath(jsonObject, "data/id");
            System.out.println("orderId为：" + orderId);
            Assertions.assertNotNull(orderId);
        }

    }


    @Test
    public void testTWPaySuccessByMC() {

        YamlReader.COUNTRY = "TW";

        if (YamlReader.verifyCurrency(YamlReader.COUNTRY)) {

            McLogin mcLogin = new McLogin(YamlReader.COUNTRY);
            String outletId = "W21G00030003";

            JsonObject jsonObject = mcLogin.NewPayOrder(outletId, "77", "hhhhh");
            System.out.println(jsonObject);
            String orderId = TestUtil.getValueByJPath(jsonObject, "data/id");
            System.out.println("orderId为：" + orderId);
            Assertions.assertNotNull(orderId);
        }

    }

    @Test
    public void testPaySuccessByOpenAPI() {

        YamlReader.COUNTRY = "SG";

        if (YamlReader.verifyCurrency(YamlReader.COUNTRY)) {
            String cookie = "xxxxx";

//            McLogin mcLogin = new McLogin(YamlReader.COUNTRY);
            OpenApi openApi = new OpenApi(YamlReader.COUNTRY, cookie);
            String outletId = "S21C00150016";


            JsonObject jsonObject = openApi.PPayOrder(cookie, outletId, "1200");
            System.out.println(jsonObject);
            String orderId = TestUtil.getValueByJPath(jsonObject, "data/id");
            String paymentId = TestUtil.getValueByJPath(jsonObject, "data/paymentId");

            System.out.println("orderId为：" + orderId);
            System.out.println("paymentId为：" + paymentId);
        }

    }

}
