package it.unina.dietideals24.model;

import java.math.BigDecimal;
import java.util.Date;

import it.unina.dietideals24.enumerations.CategoryEnum;

public class DownwardAuction extends Auction {
    private BigDecimal decreaseAmount;
    private BigDecimal minimumPrice;

    public DownwardAuction(String title, String description, CategoryEnum category, String imageURL, BigDecimal startingPrice, BigDecimal currentPrice, Long timerInMilliseconds, BigDecimal decreaseAmount, BigDecimal minimumPrice, Date createdAt) {
        super(title, description, category, imageURL, startingPrice, currentPrice, timerInMilliseconds, createdAt);
        this.decreaseAmount = decreaseAmount;
        this.minimumPrice = minimumPrice;
    }

    public BigDecimal getDecreaseAmount() {
        return decreaseAmount;
    }

    public void setDecreaseAmount(BigDecimal decreaseAmount) {
        this.decreaseAmount = decreaseAmount;
    }

    public BigDecimal getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(BigDecimal minimumPrice) {
        this.minimumPrice = minimumPrice;
    }
}
