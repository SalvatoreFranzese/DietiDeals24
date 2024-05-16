package it.unina.dietideals24.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    private Timestamp timestamp;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "offerer_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_DIETI_USER"))
    private DietiUser offerer;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "english_auction_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_ENGLISH_AUCTION"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EnglishAuction targetEnglishAuction;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "downward_auction_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_DOWNWARD_AUCTION"))
    @OnDelete(action = OnDeleteAction.CASCADE)
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
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
}