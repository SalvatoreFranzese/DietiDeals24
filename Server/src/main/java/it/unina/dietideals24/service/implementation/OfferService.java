package it.unina.dietideals24.service.implementation;

import it.unina.dietideals24.model.DietiUser;
import it.unina.dietideals24.model.EnglishAuction;
import it.unina.dietideals24.model.Offer;
import it.unina.dietideals24.repository.IOfferRepository;
import it.unina.dietideals24.service.interfaces.IOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Qualifier("mainOfferService")
public class OfferService implements IOfferService {

    private final IOfferRepository offerRepository;

    @Autowired
    public OfferService(IOfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Override
    public List<Offer> getOffersByEnglishAuctionId(Long englishAuctionId) {
        return offerRepository.findByTargetEnglishAuctionId(englishAuctionId);
    }

    @Override
    public List<Offer> getOffersByDownwardAuctionId(Long downwardAuctionId) {
        return offerRepository.findByTargetDownwardAuctionId(downwardAuctionId);
    }

    @Override
    public List<Offer> getOffersByOffererId(Long offererId) {
        return offerRepository.findByOffererId(offererId);
    }

    @Override
    public Offer save(Offer betterOffer) {
        return offerRepository.save(betterOffer);
    }

    @Override
    public Set<DietiUser> getLosers(EnglishAuction englishAuction) {
        List<Offer> losersOffers = offerRepository.findDistinctByTargetEnglishAuctionIdOrderByAmountAsc(englishAuction.getId());
        Set<DietiUser> losers = losersOffers.stream().map(Offer::getOfferer).collect(Collectors.toSet());

        Offer winnerOffer = offerRepository.findFirstDistinctByTargetEnglishAuctionIdOrderByAmountDesc(englishAuction.getId());
        losers.remove(winnerOffer.getOfferer());
        return losers;
    }

    @Override
    public DietiUser getWinner(EnglishAuction englishAuction) {
        Offer winnerOffer = offerRepository.findFirstDistinctByTargetEnglishAuctionIdOrderByAmountDesc(englishAuction.getId());
        return winnerOffer.getOfferer();
    }
}
