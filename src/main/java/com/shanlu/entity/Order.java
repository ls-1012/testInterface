package com.shanlu.entity;

public class Order {
    String id;
    Double amount;
    Double refundAmount;
    String transactionId;

    public Order() {
    }

    public Order(String transaction_id) {
        this.transactionId = transaction_id;
    }

    public Order(String id, Double amount, Double refund_amount, String transaction_id) {
        this.id = id;
        this.amount = amount;
        this.refundAmount = refund_amount;
        this.transactionId = transaction_id;
    }

    public String getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public Double getRefundAmount() {
        return refundAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }


}
