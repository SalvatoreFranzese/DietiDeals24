package it.unina.dietideals24.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class DownwardAuction extends Auction {
    private BigDecimal decreaseAmount;
    private BigDecimal minimumPrice;

    /**
     * checks if it's possibile do decrease the currentPrice by decreaseAmount
     *
     * @return true if decreaseable, false otherwise
     */
    public boolean canBeDecreased() {
        return getCurrentPrice().subtract(getDecreaseAmount()).compareTo(minimumPrice) > 0;
    }

    /**
     * Decreases currentPrice of a downwardAuction by its decreaseAmount
     * Should be used after canBeDecreased()
     */
    public void decreaseCurrentPrice() {
        setCurrentPrice(getCurrentPrice().subtract(getDecreaseAmount()));
    }
}