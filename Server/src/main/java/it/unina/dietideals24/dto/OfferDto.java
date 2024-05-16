package it.unina.dietideals24.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OfferDto {
    BigDecimal amount;
    Long offererId;
    Long auctionId;
}
