package it.unina.dietideals24.controller;

import it.unina.dietideals24.auction_timertask.DownwardAuctionTask;
import it.unina.dietideals24.model.DownwardAuction;
import it.unina.dietideals24.service.interfaces.IDownwardAuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Timer;
import java.util.logging.Logger;

@Controller
public class DownwardAuctionTimerController {
    private static final HashMap<Long, Timer> downwardAuctionTimers = new HashMap<>();
    @Qualifier("mainDownwardAuctionService")
    private final IDownwardAuctionService downwardAuctionService;
    private final FinalizePurchaseController finalizePurchaseController;
    Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    public DownwardAuctionTimerController(IDownwardAuctionService downwardAuctionService, FinalizePurchaseController finalizePurchaseController) {
        this.downwardAuctionService = downwardAuctionService;
        this.finalizePurchaseController = finalizePurchaseController;
    }

    /**
     * Starts a timer for an auction
     *
     * @param downwardAuction auction whose timer starts
     */
    public void startNewTimer(DownwardAuction downwardAuction) {
        Long position = downwardAuction.getId();
        long countdownInMilliseconds = downwardAuction.getTimerInMilliseconds();

        Timer timer = new Timer();
        DownwardAuctionTask downwardAuctionTask = new DownwardAuctionTask(finalizePurchaseController, downwardAuctionService, downwardAuction);

        timer.scheduleAtFixedRate(downwardAuctionTask, countdownInMilliseconds, countdownInMilliseconds);
        downwardAuctionTimers.put(position, timer);
        logger.info("Timer started for downward auction " + downwardAuction.getTitle());
    }

    /**
     * Stops an ongoing timer of a downwardAuction
     *
     * @param downwardAuction downwardAuction whose timer gets stopped
     */
    public void stopOngoingDownwardTimer(DownwardAuction downwardAuction) {
        Long position = downwardAuction.getId();

        Timer toBeStopped = downwardAuctionTimers.get(position);
        toBeStopped.cancel();
        toBeStopped.purge();
        downwardAuctionTimers.remove(position);
    }
}
