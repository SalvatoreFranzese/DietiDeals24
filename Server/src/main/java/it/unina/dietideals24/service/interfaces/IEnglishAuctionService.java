package it.unina.dietideals24.service.interfaces;

import it.unina.dietideals24.dto.EnglishAuctionDto;
import it.unina.dietideals24.enumeration.CategoryEnum;
import it.unina.dietideals24.model.DietiUser;
import it.unina.dietideals24.model.EnglishAuction;

import java.math.BigDecimal;
import java.util.List;

public interface IEnglishAuctionService {
    List<EnglishAuction> getEnglishAuctions();

    List<EnglishAuction> getFirst6EnglishAuctions();

    EnglishAuction getEnglishAuctionById(Long id);

    List<EnglishAuction> getEnglishAuctionsByOwner(Long ownerId);

    List<EnglishAuction> getEnglishAuctionsByCategory(CategoryEnum category);

    void deleteEnglishAuctionById(Long id);

    boolean existsById(Long id);

    EnglishAuction save(EnglishAuctionDto englishAuctionDto, DietiUser owner);

    void linkImage(String englishAuctionImageDirectory, Long id);

    void updateCurrentPrice(EnglishAuction targetAuction, BigDecimal newOffer);

    List<EnglishAuction> getByKeyword(String keyword);
}
