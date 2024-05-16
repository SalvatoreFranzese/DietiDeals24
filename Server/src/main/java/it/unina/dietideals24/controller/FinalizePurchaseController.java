package it.unina.dietideals24.controller;

import it.unina.dietideals24.enumeration.StateEnum;
import it.unina.dietideals24.model.*;
import it.unina.dietideals24.service.interfaces.IDownwardAuctionService;
import it.unina.dietideals24.service.interfaces.IEnglishAuctionService;
import it.unina.dietideals24.service.interfaces.INotificationService;
import it.unina.dietideals24.service.interfaces.IOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class FinalizePurchaseController {

    @Qualifier("mainEnglishAuctionService")
    private final IEnglishAuctionService englishAuctionService;

    @Qualifier("mainDownwardAuctionService")
    private final IDownwardAuctionService downwardAuctionService;

    @Qualifier("mainNotificationService")
    private final INotificationService notificationService;

    @Qualifier("mainOfferService")
    private final IOfferService offerService;

    @Autowired
    public FinalizePurchaseController(IEnglishAuctionService englishAuctionService, IDownwardAuctionService downwardAuctionService, INotificationService notificationService, IOfferService offerService) {
        this.englishAuctionService = englishAuctionService;
        this.downwardAuctionService = downwardAuctionService;
        this.notificationService = notificationService;
        this.offerService = offerService;
    }

    public void finalizeEnglishAuction(Long englishAuctionId) {
        EnglishAuction englishAuctionWithImage = englishAuctionService.getEnglishAuctionById(englishAuctionId);

        if (noOffersReceived(englishAuctionWithImage))
            createFailedAuctionNotification(englishAuctionWithImage);
        else
            createNotifications(englishAuctionWithImage);
        removeAuction(englishAuctionWithImage);
    }

    public void finalizeDownwardAuction(Long downwardAuctionId) {
        DownwardAuction downwardAuctionWithImage = downwardAuctionService.getDownwardAuctionById(downwardAuctionId);

        if (failed(downwardAuctionWithImage)) {
            createFailedAuctionNotification(downwardAuctionWithImage);
        } else {
            createNotifications(downwardAuctionWithImage);
        }
        removeAuction(downwardAuctionWithImage);
    }

    private void createNotifications(EnglishAuction englishAuction) {
        createLosersNotification(englishAuction);
        createWinnerNotification(englishAuction);
        createOwnerNotification(englishAuction);
    }

    private void createNotifications(DownwardAuction downwardAuction) {
        createOwnerNotification(downwardAuction);
    }

    private void createFailedAuctionNotification(Auction auction) {
        Notification failedAuctionNotification = new Notification(
                StateEnum.FALLITA,
                auction.getOwner(),
                auction.getTitle(),
                auction.getImageURL(),
                auction.getCurrentPrice()
        );
        notificationService.save(failedAuctionNotification);
    }

    private void createOwnerNotification(Auction auction) {
        Notification ownerNotification = new Notification(
                StateEnum.CONCLUSA,
                auction.getOwner(),
                auction.getTitle(),
                auction.getImageURL(),
                auction.getCurrentPrice()
        );
        notificationService.save(ownerNotification);
    }

    private void createWinnerNotification(EnglishAuction englishAuction) {
        Notification winnerNotification = new Notification(
                StateEnum.VINTA,
                offerService.getWinner(englishAuction),
                englishAuction.getTitle(),
                englishAuction.getImageURL(),
                englishAuction.getCurrentPrice()
        );
        notificationService.save(winnerNotification);
    }

    private void createLosersNotification(EnglishAuction englishAuction) {
        Set<DietiUser> losers = offerService.getLosers(englishAuction);
        List<Notification> losersNotification = new ArrayList<>();

        for (DietiUser loser : losers) {
            losersNotification.add(new Notification(
                            StateEnum.PERSA,
                            loser,
                            englishAuction.getTitle(),
                            englishAuction.getImageURL(),
                            englishAuction.getCurrentPrice()
                    )
            );
        }
        notificationService.saveAll(losersNotification);
    }

    private boolean noOffersReceived(EnglishAuction englishAuction) {
        return englishAuction.getCurrentPrice().equals(englishAuction.getStartingPrice());
    }

    private boolean failed(DownwardAuction downwardAuction) {
        return downwardAuction.getCurrentPrice().compareTo(downwardAuction.getMinimumPrice()) < 0;
    }

    private void removeAuction(EnglishAuction englishAuction) {
        englishAuctionService.deleteEnglishAuctionById(englishAuction.getId());
    }

    private void removeAuction(DownwardAuction downwardAuction) {
        downwardAuctionService.deleteDownwardAuctionById(downwardAuction.getId());
    }
}
