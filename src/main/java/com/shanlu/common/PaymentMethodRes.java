package com.shanlu.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentMethodRes {


    private List<Card> cards = new ArrayList<>();

    public PaymentMethodRes() {
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public String getFirstPaymentInstrumentId() {
        return this.cards.get(0).getAaclubPaymentInstrumentId();
    }

    public String getFirstPaymentMethodType() {
        return this.cards.get(0).getPaymentMethodType();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Card {
        private String aaclubPaymentInstrumentId;
        private String paymentMethodType;

        public Card() {
        }

        public String getAaclubPaymentInstrumentId() {
            return aaclubPaymentInstrumentId;
        }

        public void setAaclubPaymentInstrumentId(String aaclubPaymentInstrumentId) {
            this.aaclubPaymentInstrumentId = aaclubPaymentInstrumentId;
        }

        public String getPaymentMethodType() {
            return paymentMethodType;
        }

        public void setPaymentMethodType(String paymentMethodType) {
            this.paymentMethodType = paymentMethodType;
        }
    }
}
