package com.shanlu.common;

public class TransactionDetailRes {

    private Double maxRefundAmount;
    private String outletId;
    private String outletName;

    public String getOutletId() {
        return outletId;
    }

    public String getOutletName() {
        return outletName;
    }

    public TransactionDetailRes() {

    }

    public Double getMaxRefundAmount() {
        return maxRefundAmount;
    }
}
