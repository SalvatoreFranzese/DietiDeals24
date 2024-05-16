package it.unina.dietideals24.service.implementation;

import it.unina.dietideals24.dto.EnglishAuctionDto;
import it.unina.dietideals24.enumeration.CategoryEnum;
import it.unina.dietideals24.model.DietiUser;
import it.unina.dietideals24.model.EnglishAuction;
import it.unina.dietideals24.repository.IEnglishAuctionRepository;
import it.unina.dietideals24.service.interfaces.IEnglishAuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

@Service
@Qualifier("mainEnglishAuctionService")
public class EnglishAuctionService implements IEnglishAuctionService {
    private final IEnglishAuctionRepository englishAuctionRepository;

    @Autowired
    public EnglishAuctionService(IEnglishAuctionRepository englishAuctionRepository) {
        this.englishAuctionRepository = englishAuctionRepository;
    }

    @Override
    public List<EnglishAuction> getEnglishAuctions() {
        return englishAuctionRepository.findAll();
    }

    @Override
    public List<EnglishAuction> getFirst6EnglishAuctions() {
        return englishAuctionRepository.findFirst6ByOrderById();
    }

    @Override
    public EnglishAuction getEnglishAuctionById(Long id) {
        Optional<EnglishAuction> englishAuctionOptional = englishAuctionRepository.findById(id);
        if (englishAuctionOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "EnglishAuction not found");
        }
        return englishAuctionOptional.get();
    }

    @Override
    public List<EnglishAuction> getEnglishAuctionsByOwner(Long ownerId) {
        return englishAuctionRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<EnglishAuction> getEnglishAuctionsByCategory(CategoryEnum category) {
        return englishAuctionRepository.findByCategory(category);
    }

    @Override
    public void deleteEnglishAuctionById(Long id) {
        boolean exists = englishAuctionRepository.existsById(id);
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "EnglishAuction not found");
        } else {
            englishAuctionRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsById(Long id) {
        return englishAuctionRepository.existsById(id);
    }

    @Override
    public EnglishAuction save(EnglishAuctionDto englishAuctionDto, DietiUser owner) {
        EnglishAuction englishAuction = new EnglishAuction();
        englishAuction.setTitle(englishAuctionDto.getTitle());
        englishAuction.setDescription(englishAuctionDto.getDescription());
        englishAuction.setCategory(englishAuctionDto.getCategory());
        englishAuction.setTimerInMilliseconds(englishAuctionDto.getTimerInMilliseconds());
        englishAuction.setCurrentPrice(englishAuctionDto.getCurrentPrice());
        englishAuction.setStartingPrice(englishAuctionDto.getStartingPrice());
        englishAuction.setIncreaseAmount(englishAuctionDto.getIncreaseAmount());
        englishAuction.setOwner(owner);

        return englishAuctionRepository.save(englishAuction);
    }

    @Override
    public void linkImage(String englishAuctionImageDirectory, Long id) {
        Optional<EnglishAuction> englishAuctionOptional = englishAuctionRepository.findById(id);
        if (englishAuctionOptional.isPresent()) {
            EnglishAuction englishAuction = englishAuctionOptional.get();
            englishAuction.setImageURL(englishAuctionImageDirectory + File.separatorChar + id + ".jpeg");
            englishAuctionRepository.save(englishAuction);
        }
    }

    @Override
    public void updateCurrentPrice(EnglishAuction targetAuction, BigDecimal newOffer) {
        targetAuction.setCurrentPrice(newOffer);
        targetAuction.setCreatedAt(new Date(System.currentTimeMillis()));
        englishAuctionRepository.save(targetAuction);
    }

    @Override
    public List<EnglishAuction> getByKeyword(String keyword) {
        Set<EnglishAuction> foundAuctions = new HashSet<>();
        foundAuctions.addAll(englishAuctionRepository.findByTitleContainsIgnoreCase(keyword));
        foundAuctions.addAll(englishAuctionRepository.findByDescriptionContainsIgnoreCase(keyword));
        return foundAuctions.stream().toList();
    }
}
