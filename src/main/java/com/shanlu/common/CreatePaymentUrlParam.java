package com.shanlu.common;

import java.math.BigDecimal;

public class CreatePaymentUrlParam {
    BigDecimal amount;
    String outletId;
    String eCommerceOrderId;

    public CreatePaymentUrlParam(BigDecimal amount, String outletId, String eCommerceOrderId) {
        this.amount = amount;
        this.outletId = outletId;
        this.eCommerceOrderId = eCommerceOrderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getOutletId() {
        return outletId;
    }

    public String geteCommerceOrderId() {
        return eCommerceOrderId;
    }
}
