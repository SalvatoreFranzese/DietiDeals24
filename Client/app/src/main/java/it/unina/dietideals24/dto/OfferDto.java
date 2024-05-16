package it.unina.dietideals24.dto;

import java.math.BigDecimal;

public class OfferDto {
    BigDecimal amount;
    Long offererId;
    Long auctionId;

    public OfferDto(BigDecimal amount, Long offererId, Long auctionId) {
        this.amount = amount;
        this.offererId = offererId;
        this.auctionId = auctionId;
    }
}
