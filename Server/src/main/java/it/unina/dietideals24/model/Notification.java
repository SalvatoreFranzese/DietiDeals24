package it.unina.dietideals24.model;

import it.unina.dietideals24.enumeration.StateEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private StateEnum state;
    private String titleOfTheAuction;
    private String imageUrlOfTheAuction;
    private BigDecimal finalPrice;
    private boolean pushed;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_DIETI_USER"))
    private DietiUser receiver;

    public Notification(Long id, StateEnum state, DietiUser receiver, String titleOfTheAuction, String imageUrlOfTheAuction, BigDecimal finalPrice, boolean pushed) {
        this.id = id;
        this.state = state;
        this.receiver = receiver;
        this.titleOfTheAuction = titleOfTheAuction;
        this.imageUrlOfTheAuction = imageUrlOfTheAuction;
        this.finalPrice = finalPrice;
        this.pushed = pushed;
    }

    public Notification(StateEnum state, DietiUser receiver, String titleOfTheAuction, String imageUrlOfTheAuction, BigDecimal finalPrice) {
        this.state = state;
        this.receiver = receiver;
        this.titleOfTheAuction = titleOfTheAuction;
        this.imageUrlOfTheAuction = imageUrlOfTheAuction;
        this.finalPrice = finalPrice;
        this.pushed = false;
    }
}