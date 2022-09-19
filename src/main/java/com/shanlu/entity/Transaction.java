package com.shanlu.entity;

public class Transaction {
    String id;
    String referenceId;
    String merchantId;
    String amount;
    String originalOrderId;

    public String getId() {
        return id;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getAmount() {
        return amount;
    }

    public String getOriginalOrderId() {
        return originalOrderId;
    }

    public void setReference_id(String reference_id) {
        this.referenceId = referenceId;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchantId = merchantId;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setOriginal_order_id(String original_order_id) {
        this.originalOrderId = originalOrderId;
    }

    public Transaction() {
    }

    public Transaction(String id, String referenceId, String merchantId, String amount, String originalOrderId) {
        this.id = id;
        this.referenceId = referenceId;
        this.merchantId = merchantId;
        this.amount = amount;
        this.originalOrderId = originalOrderId;
    }
}
