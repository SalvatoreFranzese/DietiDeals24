package it.unina.dietideals24.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Offer {
    private Long id;
    private BigDecimal amount;
    private Timestamp timestamp;
    private DietiUser offerer;
    private EnglishAuction targetEnglishAuction;
    private DownwardAuction targetDownwardAuction;

    public Offer(BigDecimal amount, DietiUser offerer, EnglishAuction targetEnglishAuction) {
        this.amount = amount;
        this.offerer = offerer;
        this.targetEnglishAuction = targetEnglishAuction;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public Offer(BigDecimal amount, DietiUser offerer, DownwardAuction targetDownwardAuction) {
        this.amount = amount;
        this.offerer = offerer;
        this.targetDownwardAuction = targetDownwardAuction;
    }

    public Offer(Long id, BigDecimal amount, Timestamp timestamp, DietiUser offerer, EnglishAuction targetEnglishAuction, DownwardAuction targetDownwardAuction) {
        this.id = id;
        this.amount = amount;
        this.timestamp = timestamp;
        this.offerer = offerer;
        this.targetEnglishAuction = targetEnglishAuction;
        this.targetDownwardAuction = targetDownwardAuction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public DietiUser getOfferer() {
        return offerer;
    }

    public void setOfferer(DietiUser offerer) {
        this.offerer = offerer;
    }

    public EnglishAuction getTargetEnglishAuction() {
        return targetEnglishAuction;
    }

    public void setTargetEnglishAuction(EnglishAuction targetEnglishAuction) {
        this.targetEnglishAuction = targetEnglishAuction;
    }

    public DownwardAuction getTargetDownwardAuction() {
        return targetDownwardAuction;
    }

    public void setTargetDownwardAuction(DownwardAuction targetDownwardAuction) {
        this.targetDownwardAuction = targetDownwardAuction;
    }
}
