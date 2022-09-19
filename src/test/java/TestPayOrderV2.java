import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanlu.business.AppUserV2;
import com.shanlu.business.MerchantCenterUser;
import com.shanlu.common.CreatePaymentUrlParam;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;

public class TestPayOrderV2 {

    private final CloseableHttpClient client = HttpClients.createDefault();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSGPaySuccessByMCV2() throws IOException, URISyntaxException {


        MerchantCenterUser mcUser = new MerchantCenterUser(client, objectMapper, "xxx", "xxx", "xxxxxx");
        AppUserV2 appUser = new AppUserV2(client, objectMapper, "111", "2222", "xxxxxxx");

        for (int i = 0; i < 20; i++) {
            int dollar = ThreadLocalRandom.current().nextInt(20, 151);
            int cent = ThreadLocalRandom.current().nextInt(0, 100);

            BigDecimal amount = new BigDecimal(dollar + "." + cent);

            long startCreatePayment = System.currentTimeMillis();
            URL paymentUrl = mcUser.createPaymentUrl(new CreatePaymentUrlParam(amount, "S21C00150002", "some string 1234"));
            long endCreatePayment = System.currentTimeMillis();
            System.out.println("Create payment link took " + (endCreatePayment - startCreatePayment) + "ms.");
            System.out.println(paymentUrl.toString());
            long startMakePayment = System.currentTimeMillis();
            appUser.makePayment(paymentUrl);
            long endMakePayment = System.currentTimeMillis();
            System.out.println("Making payment took " + (endMakePayment - startMakePayment) + "ms.");
        }
    }

}
