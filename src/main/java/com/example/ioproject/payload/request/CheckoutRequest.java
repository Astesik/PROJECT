package com.example.ioproject.payload.request;

public class CheckoutRequest {

    private String carName;
    private Long amount;

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

}
