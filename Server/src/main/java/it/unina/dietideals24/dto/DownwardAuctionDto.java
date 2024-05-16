package it.unina.dietideals24.dto;

import it.unina.dietideals24.enumeration.CategoryEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DownwardAuctionDto {
    private String title;
    private String description;
    private CategoryEnum category;
    private BigDecimal startingPrice;
    private BigDecimal currentPrice;
    private Long timerInMilliseconds;
    private BigDecimal decreaseAmount;
    private BigDecimal minimumPrice;
    private Long ownerId;
}
