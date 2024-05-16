package it.unina.dietideals24.controller;

import it.unina.dietideals24.auction_timertask.EnglishAuctionTask;
import it.unina.dietideals24.model.EnglishAuction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Timer;
import java.util.logging.Logger;

@Controller
public class EnglishAuctionTimerController {

    private static final HashMap<Long, Timer> englishAuctionTimers = new HashMap<>();
    private final FinalizePurchaseController finalizePurchaseController;
    Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    public EnglishAuctionTimerController(FinalizePurchaseController finalizePurchaseController) {
        this.finalizePurchaseController = finalizePurchaseController;
    }

    /**
     * Starts a timer for an auction
     *
     * @param auction auction whose timer starts
     */
    public void startNewTimer(EnglishAuction auction) {
        Long auctionId = auction.getId();
        long countdownInMilliseconds = auction.getTimerInMilliseconds();

        Timer timer = new Timer();

        EnglishAuctionTask englishAuctionTask = new EnglishAuctionTask(finalizePurchaseController, auction);
        timer.schedule(englishAuctionTask, countdownInMilliseconds);

        englishAuctionTimers.put(auctionId, timer);
        logger.info("Timer started for english auction " + auction.getTitle());
    }

    /**
     * Restarts the timer of an englishAuction
     *
     * @param englishAuction englishAuction whose timer gets restarted
     */
    public void restartOngoingEnglishTimer(EnglishAuction englishAuction) {
        Long auctionId = englishAuction.getId();
        Long countdownInMilliseconds = englishAuction.getTimerInMilliseconds();

        Timer toBeRestarted = englishAuctionTimers.get(auctionId);
        toBeRestarted.cancel();

        toBeRestarted = new Timer();

        EnglishAuctionTask englishAuctionTask = new EnglishAuctionTask(finalizePurchaseController, englishAuction);

        toBeRestarted.schedule(englishAuctionTask, countdownInMilliseconds);
        logger.info("Timer restarted for english auction " + englishAuction.getTitle());
    }
}
