package com.shanlu.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicPaymentRes {
    private String paymentId;
    private List<Plan> plans;

    public PublicPaymentRes() {
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getFirstProductId() {
        return this.plans.get(0).getProductId();
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Plan {
        private String productId;

        public Plan() {
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }
    }
}
