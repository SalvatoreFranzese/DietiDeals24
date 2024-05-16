package it.unina.dietideals24.model;

import java.math.BigDecimal;
import java.util.Date;

import it.unina.dietideals24.enumerations.CategoryEnum;

public class EnglishAuction extends Auction {
    private BigDecimal increaseAmount;

    public EnglishAuction(String title, String description, CategoryEnum category, String imageURL, BigDecimal startingPrice, BigDecimal currentPrice, Long timerInMilliseconds, BigDecimal increaseAmount, Date createdAt) {
        super(title, description, category, imageURL, startingPrice, currentPrice, timerInMilliseconds, createdAt);
        this.increaseAmount = increaseAmount;
    }

    public BigDecimal getIncreaseAmount() {
        return increaseAmount;
    }

    public void setIncreaseAmount(BigDecimal increaseAmount) {
        this.increaseAmount = increaseAmount;
    }
}
