import com.google.gson.JsonObject;
import com.shanlu.mapper.MerchantMapper;
import com.shanlu.util.CSVUtil;

import com.shanlu.util.MyDateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.util.*;

public class TestCSVReport {
    MerchantMapper merchantMapper = new MerchantMapper();
    @Test
    public void testCsv(){

        try {
            FileReader fileReader = new FileReader("/Users/shanlu/develop/T22E0007_transactions__1660555661201.csv");
            List<JsonObject> jsonObjects = CSVUtil.convertCscToJson(fileReader);
            System.out.println(jsonObjects);
            String startDate = "2022-03-04";

            String  startDateStr = MyDateUtil.getStrateTimeStamp(startDate);
            String  endDateStr = MyDateUtil.getEndTimeStamp(startDate);

            List<String> settlementPayoutIdsByDate = merchantMapper.getSettlementPayoutIdsByDate(startDateStr, endDateStr);
            System.out.println(settlementPayoutIdsByDate);
            Iterator<String> iterator = settlementPayoutIdsByDate.iterator();
            while(iterator.hasNext()){
                String payoutId = iterator.next();
                List<HashMap<String, String>> settlementItemsByPayoutId = merchantMapper.getSettlementItemsByPayoutId(payoutId);
                //思路：后续把csv 中涉及的字段会从数据库中读取出来，组装成json，使用json比对工具验证。
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }
}
