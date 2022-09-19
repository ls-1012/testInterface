package com.shanlu.common;

public class MakePaymentParam {
    private String paymentId;
    private String paymentMethodType;
    private String productId;
    private Object voucherUserRecordIds;
    private String cardId;

    public MakePaymentParam() {
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(String paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Object getVoucherUserRecordIds() {
        return voucherUserRecordIds;
    }

    public void setVoucherUserRecordIds(Object voucherUserRecordIds) {
        this.voucherUserRecordIds = voucherUserRecordIds;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
}
