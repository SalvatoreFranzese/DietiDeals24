package it.unina.dietideals24.service.interfaces;

import it.unina.dietideals24.model.DietiUser;

import java.util.List;

public interface IDietiUserService {
    List<DietiUser> getUsers();

    DietiUser getUserById(Long dietiUserId);

    void addNewDietiUser(DietiUser dietiUser);

    void deleteDietiUser(Long dietiUserId);

    boolean existsById(Long id);

    void linkImage(String profilePicDirectory, Long id);

    DietiUser getUserByEmail(String email);

    DietiUser updateDietiUserData(DietiUser toBeUpdated, DietiUser newDietiUser);

    DietiUser updateDietiUserPassword(DietiUser toBeUpdated, String encodedPassword);
}
