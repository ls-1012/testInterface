import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shanlu.business.MbossLogin;
import com.shanlu.util.TestUtil;
import com.shanlu.util.YamlReader;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestMboss {

    @Test
    public void testCreateNormalCompany(){
        /**
         * 1、从0直接点击submit，创建company----normal
         * 2、从0直接点击save，创建company----saved
         * 3、从saved状态点击submit，创建company-----normal
         */
        YamlReader.COUNTRY = "SG";

        if(YamlReader.verifyCurrency(YamlReader.COUNTRY)) {
            String cookie = "xxxxx";
            MbossLogin mbossLogin = new MbossLogin(cookie, YamlReader.COUNTRY);
            String companyId = mbossLogin.createNormalCompany("", "");
            System.out.println("companyId: "+companyId);
            Assertions.assertNotNull(companyId);
        }

    }

    @Test
    public void testCreateSaveCompnay(){
        /**
         *
         */
        YamlReader.COUNTRY = "SG";

        if(YamlReader.verifyCurrency(YamlReader.COUNTRY)) {
            String cookie = "xxxxx";
//            TW
//            String cookie = "Authorization=eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyTmFtZSI6InNoYW4ubHUiLCJpc3MiOiJBQUNMVUIiLCJpYXQiOjE2NTc5NTk5NzIsImV4cCI6MTY1OTE2OTU3Mn0.exX3K8kn2U6sDFiRwrxMM9WStcW_UCeao1Mi_olqlHSsFK-I7vSaMihrmBtmgM-IGz3otNwBrNMEPivqlvhjww; ADVSSO=tw-mboss.apaylater.net+.XuYvweQDy4P";
            MbossLogin mbossLogin = new MbossLogin(cookie, YamlReader.COUNTRY);
            String companyId = mbossLogin.createSavedCompany("", "");
            System.out.println("companyId: "+companyId);
            Assertions.assertNotNull(companyId);
        }
    }

    @Test
    public void testSaveOfflineOutlet(){
        /**
         *
         */
        YamlReader.COUNTRY = "TH";

        if(YamlReader.verifyCurrency(YamlReader.COUNTRY)) {
            String cookie = "xxxxx";
            MbossLogin mbossLogin = new MbossLogin(cookie, YamlReader.COUNTRY);
            String outletId = mbossLogin.createOfflineSaveOutlet("T22E0007", "", "");
            System.out.println("outletId: "+outletId);
            Assertions.assertNotNull(outletId);
        }
    }

    @Test
    public void testSaveOnlineOutlet(){
        /**
         *
         */
        YamlReader.COUNTRY = "TH";

        if(YamlReader.verifyCurrency(YamlReader.COUNTRY)) {
            String cookie = "xxxxx";
            MbossLogin mbossLogin = new MbossLogin(cookie, YamlReader.COUNTRY);
            String outletId = mbossLogin.createOnlineSaveOutlet("T22E0007", "", "");
            System.out.println("outletId: "+outletId);
            Assertions.assertNotNull(outletId);
        }
    }

    @Test
    public void testEnableOfflineOutlet(){
        /**
         *
         */
        YamlReader.COUNTRY = "SG";

        if(YamlReader.verifyCurrency(YamlReader.COUNTRY)) {
            String cookie = "xxxxx";
            MbossLogin mbossLogin = new MbossLogin(cookie, YamlReader.COUNTRY);
            String outletId = mbossLogin.createOfflineEnableOutlet("T22E0007", "", "");
            System.out.println("outletId: "+outletId);
            Assertions.assertNotNull(outletId);
        }
    }

    @Test
    public void testEnableOnlineOutlet(){
        /**
         *
         */
        YamlReader.COUNTRY = "SG";

        if(YamlReader.verifyCurrency(YamlReader.COUNTRY)) {
            String cookie = "xxxxx";
            MbossLogin mbossLogin = new MbossLogin(cookie, YamlReader.COUNTRY);
            String outletId = mbossLogin.createOnlineEnableOutlet("S22G0046", "", "");
            System.out.println("outletId: "+outletId);
            Assertions.assertNotNull(outletId);
        }
    }

    @Test
    public void testCreateBrand(){
        YamlReader.COUNTRY = "TH";

        if(YamlReader.verifyCurrency(YamlReader.COUNTRY)) {
            String cookie = "xxxxx";
            MbossLogin mbossLogin = new MbossLogin(cookie, YamlReader.COUNTRY);
            String merchantBrand = mbossLogin.createMerchantBrand();

            System.out.println("merchantBrand: "+merchantBrand);
            Assertions.assertNotNull(merchantBrand);
        }
    }

    @Test
    public void testSetOutletParam(){
        String param="{}";
        JsonObject paramJson = TestUtil.formatParam(param);
        JsonObject basic = paramJson.get("basic").getAsJsonObject();
        basic.addProperty("belongingsTo","123");
        basic.addProperty("companyId","123");
        basic.addProperty("merchantName","aaaaa");
        basic.addProperty("displayName","bbbbb");

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

        System.out.println("create outlet param: "+paramJson);
    }


}
