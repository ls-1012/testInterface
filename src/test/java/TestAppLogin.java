import com.google.gson.JsonObject;
import com.shanlu.business.AppLogin;
import com.shanlu.util.TestUtil;
import com.shanlu.util.YamlReader;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestAppLogin {

    @Test
    public void testAppLoginSuccess(){
        YamlReader.COUNTRY = "TW";
        AppLogin appLogin = new AppLogin(YamlReader.COUNTRY);
        CloseableHttpResponse loginRes = appLogin.appLogin();
        JsonObject login = TestUtil.getResponseBody(loginRes);
        int statusCode = TestUtil.getStatusCode(loginRes);

        System.out.println(login);
        Assertions.assertEquals(statusCode,200);
    }
}

