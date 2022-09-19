import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shanlu.business.McLogin;
import com.shanlu.mapper.MerchantMapper;
import com.shanlu.util.TestUtil;
import com.shanlu.util.YamlReader;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestMerchant {

    McLogin mcLogin;

    /**
     * 需要执行./port.sh xxx
     */

    @Test
    public void test1(){
        YamlReader.COUNTRY ="SG";
        mcLogin = new McLogin(YamlReader.COUNTRY);
        String companyId = "S21D0063";
        MerchantMapper merchant = new MerchantMapper();
//        List<HashMap<String, String>> merchantList = merchant.searchOnlineAndNormalAndToMerchantOutletId(companyId);
//        Gson gson = new Gson();
//        String s = gson.toJson(merchantList);//转换成了json类型
        List<String> merchantList = merchant.searchOnlineAndNormalAndToMerchantOutletId(companyId);
        Gson gson = new Gson();
        String s = gson.toJson(merchantList);//转换成了json 字符串类型
        System.out.println(s);
    }

    @Test
    public void testMe(){

        CloseableHttpResponse me = mcLogin.getMe();

        JsonObject meRes = TestUtil.getResponseBody(me);
        System.out.println(meRes);

    }

    @Test
    public void testProfile(){

        CloseableHttpResponse profile = mcLogin.getProfile();

        JsonObject meRes = TestUtil.getResponseBody(profile);
        System.out.println(meRes);

    }

    @Test
    public void testOutles(){

        CloseableHttpResponse outlets = mcLogin.getOutlets();

        JsonObject meRes = TestUtil.getResponseBody(outlets);
        System.out.println(meRes);

    }


    @Test
    public void testTransactionsList(){

        CloseableHttpResponse transacitonsList = mcLogin.getTransacitonsList("2021-12-01", "2021-12-23", null);

        JsonObject meRes = TestUtil.getResponseBody(transacitonsList);
        System.out.println(meRes);

    }

    @Test
    public void testTransactionsDetail(){
        CloseableHttpResponse transacitonsList = mcLogin.getTransactionDetail("MTX1138519166110");

        JsonObject meRes = TestUtil.getResponseBody(transacitonsList);
        System.out.println(meRes);

    }

    @Test
    public void testOverview(){
        CloseableHttpResponse transacitonsList = mcLogin.getOverviewByAllOutlet(null,null);

        JsonObject meRes = TestUtil.getResponseBody(transacitonsList);
        System.out.println(meRes);

    }

    @Test
    public void testPayoutList(){
        CloseableHttpResponse transacitonsList = mcLogin.getPayoutList(null,null);

        JsonObject meRes = TestUtil.getResponseBody(transacitonsList);
        System.out.println(meRes);

    }
}
