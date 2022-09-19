package com.shanlu.mapper;


import com.shanlu.config.BaseDAO;
import com.shanlu.entity.SettlementItems;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;


public class MerchantMapper {

    public List<String> searchOnlineAndNormalAndToMerchantOutletId(String companyId) {
        String sql = "SELECT merchant_id FROM atome_afterpay_merchant.merchant WHERE settlement_type ='MERCHANT' \n" +
            "AND merchant_type = 'ONLINE'\n" +
            "AND `status` = 'ENABLED'\n" +
            "AND company_id =?\n" +
            "AND activate_time is not NULL";
        System.out.println("sql: "+sql);
        System.out.println("companyId: "+companyId);
        List<String> resuleSet = BaseDAO.excutQuerySingleResult(sql, companyId);
//        System.out.println(resuleSet);
        return resuleSet;

    }

    public List<String> searchOnlineAndNormalAndToPspOutletId(String companyId) {
        String sql = "SELECT merchant_id FROM merchant WHERE settlement_type ='PSP' \n" +
            "AND merchant_type = 'ONLINE'\n" +
            "AND `status` = 'ENABLED'\n" +
            "AND company_id =?\n" +
            "AND activate_time is not NULL";
        System.out.println("sql: "+sql);
        System.out.println("companyId: "+companyId);
        List<String> resuleSet = BaseDAO.excutQuerySingleResult(sql, companyId);
//        System.out.println(resuleSet);
        return resuleSet;

    }

    public List<String> searchOnlineAndNormalAndToPspOutletId1() {
        String sql = "SELECT merchant_id FROM merchant WHERE settlement_type ='PSP' \n" +
            "AND merchant_type = 'ONLINE'\n" +
            "AND `status` = 'ENABLED'\n" +
            "AND activate_time is not NULL";
        System.out.println("sql: "+sql);
        List<String> resuleSet = BaseDAO.excutQuerySingleResult(sql);
//        System.out.println(resuleSet);
        return resuleSet;

    }
    public String getOrderIdByTransactionId(String id) {
        String sql = "select original_order_id from merchant_center_transaction where id=?";
        System.out.println("sql: "+sql);
        System.out.println("companyId: "+id);
        List<String> resuleSet = BaseDAO.excutQuerySingleResult(sql, id);
//        System.out.println(resuleSet);
        return resuleSet.get(0);
    }

    public String getTransactionIdByOrderId(String orderId) {
        String sql = "select id from merchant_center_transaction where transaction_code='TC110' and original_order_id=? ";
        System.out.println("sql: " + sql);
        System.out.println("orderId: " + orderId);
        List<String> resuleSet = BaseDAO.excutQuerySingleResult(sql, orderId);
//        System.out.println(resuleSet);
        return resuleSet.get(0);
    }

    public List<String> getSettlementPayoutIdsByDate(String startDate,String endDate) {
        String sql = "select id from settlement_payout where payout_submit_time between ? and ? and settlement_status = settled ";
        System.out.println("sql: " + sql);
        System.out.println("startDate: " + startDate);
        System.out.println("startDate: " + endDate);
        List<String> resuleSet = BaseDAO.excutQuerySingleResult(sql, startDate,endDate);
//        System.out.println(resuleSet);
        return resuleSet;
    }

    public List<HashMap<String, String>> getSettlementItemsByPayoutId(String payoutId) {
        String sql = "select * from settlement_item where payoutId = ? and settlement_status = settled ";
        System.out.println("sql: " + sql);
        System.out.println("payoutId: " + payoutId);
//        System.out.println("startDate: " + endDate);
        List<HashMap<String, String>> settlment_item = BaseDAO.excutQuery(sql, "settlment_item", payoutId);
//        System.out.println(resuleSet);
        return settlment_item;
    }

    public String getReferenceIdByPaymentId(String paymentId) {
        String sql = "select reference_id from atome_afterpay_payment.third_party_payment where payment_id=? ";
        System.out.println("sql: " + sql);
        System.out.println("paymentId: " + paymentId);
        List<String> resuleSet = BaseDAO.excutQuerySingleResult(sql, paymentId);
//        System.out.println(resuleSet);
        return resuleSet.get(0);
    }

//    @Select("select original_order_id from merchant_center_transaction where id=#{id}")
//    String getOrderIdByTransactionId(@Param("id")String id);

//    @Select("select id from merchant_center_transaction where original_order_id=#{orderId}")
//    String getTransactionIdByOrderId(@Param("orderId")String orderId);

//    @Select("select original_order_id from merchant_center_transaction where id=#{id}")
//    String getOrderIdByTransactionId(@Param("id")String id);
//
//    @Select("select id from merchant_center_transaction where original_order_id=#{orderId}")
//    String getTransactionIdByOrderId(@Param("orderId")String orderId);

//
//
//    @Select("SELECT merchant_id FROM merchant WHERE settlement_type ='MERCHANT' \n" +
//        "AND merchant_type = 'ONLINE'\n" +
//        "AND `status` = 'ENABLED'\n" +
//        "AND company_id = #{companyId}\n" +
//        "AND activate_time is not NULL")
//    List<String> searchOnlineAndNormalAndToMerchantOutletId(@Param("companyId")String companyId);
//
//    @Select("SELECT merchant_id FROM merchant WHERE settlement_type ='PSP' \n" +
//        "AND merchant_type ='ONLINE'\n" +
//        "AND `status` ='ENABLED'\n" +
//        "AND company_id = #{companyId}\n" +
//        "AND activate_time is not NULL")
//    List<String> searchOnlineAndNormalAndToPspOutletId(@Param("companyId")String companyId);
//
//
//    @Select("SELECT merchant_id FROM merchant WHERE settlement_type ='PSP' \n" +
//        "AND merchant_type ='ONLINE'\n" +
//        "AND `status` ='ENABLED'\n" +
//        "AND activate_time is not NULL")
//    List<String> searchOnlineAndNormalAndToPspOutletId1();

}
