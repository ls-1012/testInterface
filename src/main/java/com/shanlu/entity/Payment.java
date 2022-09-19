package com.shanlu.entity;

public class Payment {
    String referenceId;
    String currency;
    Double amount;
    Double refundableAmount;
    String status;

    public String getReferenceId() {
        return referenceId;
    }

    public String getCurrency() {
        return currency;
    }

    public Double getAmount() {
        return amount;
    }

    public Double getRefundableAmount() {
        return refundableAmount;
    }

    public String getStatus() {
        return status;
    }
}
