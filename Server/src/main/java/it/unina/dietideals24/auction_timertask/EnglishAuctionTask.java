package it.unina.dietideals24.auction_timertask;

import it.unina.dietideals24.controller.FinalizePurchaseController;
import it.unina.dietideals24.model.EnglishAuction;
import lombok.Getter;
import lombok.Setter;

import java.util.TimerTask;

@Getter
@Setter
public class EnglishAuctionTask extends TimerTask {

    private FinalizePurchaseController finalizePurchaseController;
    private EnglishAuction auction;

    public EnglishAuctionTask(FinalizePurchaseController finalizePurchaseController, EnglishAuction auction) {
        this.finalizePurchaseController = finalizePurchaseController;
        this.auction = auction;
    }

    @Override
    public void run() {
        finalizePurchaseController.finalizeEnglishAuction(auction.getId());
        cancel();
    }
}
