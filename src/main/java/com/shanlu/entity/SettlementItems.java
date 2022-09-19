package com.shanlu.entity;

public class SettlementItems {
    String id;
    String payoutId;
    String transactionId;
    String settlementitem;
    String status;
    String bussinessCode;

    public void setId(String id) {
        this.id = id;
    }

    public void setPayoutId(String payoutId) {
        this.payoutId = payoutId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setSettlementitem(String settlementitem) {
        this.settlementitem = settlementitem;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBussinessCode(String bussinessCode) {
        this.bussinessCode = bussinessCode;
    }

    public String getId() {
        return id;
    }

    public String getPayoutId() {
        return payoutId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getSettlementitem() {
        return settlementitem;
    }

    public String getStatus() {
        return status;
    }

    public String getBussinessCode() {
        return bussinessCode;
    }
}
