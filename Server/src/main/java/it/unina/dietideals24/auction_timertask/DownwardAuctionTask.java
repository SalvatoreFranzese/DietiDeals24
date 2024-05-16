package it.unina.dietideals24.auction_timertask;

import it.unina.dietideals24.controller.FinalizePurchaseController;
import it.unina.dietideals24.model.DownwardAuction;
import it.unina.dietideals24.service.interfaces.IDownwardAuctionService;
import lombok.Getter;
import lombok.Setter;

import java.util.TimerTask;

@Getter
@Setter
public class DownwardAuctionTask extends TimerTask {

    private FinalizePurchaseController finalizePurchaseController;
    private IDownwardAuctionService downwardAuctionService;
    private DownwardAuction downwardAuction;

    public DownwardAuctionTask(FinalizePurchaseController finalizePurchaseController, IDownwardAuctionService downwardAuctionService, DownwardAuction downwardAuction) {
        this.finalizePurchaseController = finalizePurchaseController;
        this.downwardAuctionService = downwardAuctionService;
        this.downwardAuction = downwardAuction;
    }

    @Override
    public void run() {
        if (downwardAuction.canBeDecreased()) {
            downwardAuctionService.decreaseCurrentPrice(getDownwardAuction().getId());
            downwardAuction.decreaseCurrentPrice();
        } else {
            downwardAuction.decreaseCurrentPrice();
            finalizePurchaseController.finalizeDownwardAuction(downwardAuction.getId());
            cancel();
        }
    }
}
