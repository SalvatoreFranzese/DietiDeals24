package it.unina.dietideals24.repository;

import it.unina.dietideals24.enumeration.CategoryEnum;
import it.unina.dietideals24.model.EnglishAuction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEnglishAuctionRepository extends JpaRepository<EnglishAuction, Long> {
    List<EnglishAuction> findFirst6ByOrderById();

    List<EnglishAuction> findByOwnerId(Long ownerId);

    List<EnglishAuction> findByCategory(CategoryEnum category);

    List<EnglishAuction> findByTitleContainsIgnoreCase(String keyword);

    List<EnglishAuction> findByDescriptionContainsIgnoreCase(String keyword);
}