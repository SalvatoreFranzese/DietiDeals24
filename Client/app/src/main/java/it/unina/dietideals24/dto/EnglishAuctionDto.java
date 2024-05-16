package it.unina.dietideals24.dto;

import java.math.BigDecimal;

import it.unina.dietideals24.enumerations.CategoryEnum;

public class EnglishAuctionDto {
    private String title;
    private String description;
    private CategoryEnum category;
    private BigDecimal startingPrice;
    private BigDecimal currentPrice;
    private Long timerInMilliseconds;
    private BigDecimal increaseAmount;
    private Long ownerId;

    public EnglishAuctionDto(String title, String description, CategoryEnum category, BigDecimal startingPrice, BigDecimal currentPrice, Long timerInMilliseconds, BigDecimal increaseAmount, Long ownerId) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.startingPrice = startingPrice;
        this.currentPrice = currentPrice;
        this.timerInMilliseconds = timerInMilliseconds;
        this.increaseAmount = increaseAmount;
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(BigDecimal startingPrice) {
        this.startingPrice = startingPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Long getTimerInMilliseconds() {
        return timerInMilliseconds;
    }

    public void setTimerInMilliseconds(Long timerInMilliseconds) {
        this.timerInMilliseconds = timerInMilliseconds;
    }

    public BigDecimal getIncreaseAmount() {
        return increaseAmount;
    }

    public void setIncreaseAmount(BigDecimal increaseAmount) {
        this.increaseAmount = increaseAmount;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}

