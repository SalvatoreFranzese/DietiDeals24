package it.unina.dietideals24.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class EnglishAuction extends Auction {
    private BigDecimal increaseAmount;
}