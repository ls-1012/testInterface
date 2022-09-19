import com.shanlu.business.AppLogin;
import com.shanlu.business.McLogin;
import com.shanlu.util.YamlReader;
import org.junit.jupiter.api.Test;

public class TestMcLogin {

    @Test
    public void testMcLoginSuccess(){
        YamlReader.COUNTRY = "SG";
        McLogin mcLogin = new McLogin(YamlReader.COUNTRY);
        System.out.println(mcLogin.login());
    }
}
