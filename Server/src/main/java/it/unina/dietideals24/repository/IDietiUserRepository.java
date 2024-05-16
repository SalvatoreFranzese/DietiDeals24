package it.unina.dietideals24.repository;

import it.unina.dietideals24.model.DietiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IDietiUserRepository extends JpaRepository<DietiUser, Long> {

    //@Query("SELECT u FROM DietiUser d WHERE d.email = ?1") //JPQL
    //DietiUser nella query è quella taggata con @Entity
    boolean existsByEmail(String email);

    Optional<DietiUser> findByEmail(String email);
}
