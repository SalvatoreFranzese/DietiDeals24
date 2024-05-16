package it.unina.dietideals24.model;

import java.math.BigDecimal;

import it.unina.dietideals24.enumerations.StateEnum;

public class Notification {
    private Long id;
    private StateEnum state;
    private DietiUser receiver;
    private String titleOfTheAuction;
    private String imageUrlOfTheAuction;
    private BigDecimal finalPrice;
    private boolean pushed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StateEnum getState() {
        return state;
    }

    public void setState(StateEnum state) {
        this.state = state;
    }

    public DietiUser getReceiver() {
        return receiver;
    }

    public void setReceiver(DietiUser receiver) {
        this.receiver = receiver;
    }

    public String getTitleOfTheAuction() {
        return titleOfTheAuction;
    }

    public void setTitleOfTheAuction(String titleOfTheAuction) {
        this.titleOfTheAuction = titleOfTheAuction;
    }

    public String getImageUrlOfTheAuction() {
        return imageUrlOfTheAuction;
    }

    public void setImageUrlOfTheAuction(String imageUrlOfTheAuction) {
        this.imageUrlOfTheAuction = imageUrlOfTheAuction;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public boolean isPushed() {
        return pushed;
    }

    public void setPushed(boolean pushed) {
        this.pushed = pushed;
    }
}
