package it.unina.dietideals24.service.implementation;

import it.unina.dietideals24.model.DietiUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DietiUserServiceTest {

    DietiUserService dietiUserService = new DietiUserService();

    DietiUser toBeUpdated;
    DietiUser newDietiUser;

    @BeforeEach
    void setup() {
        toBeUpdated = new DietiUser("Mario", "Rossi", "example@mail.com", "foo", "Napoli", null);
        newDietiUser = new DietiUser("Mariano", "Rossi", "example@mail.com", "Extended biography", "Napoli", null);
    }

    @Test
    void updateData_covers_1_1() {
        dietiUserService.updateData(toBeUpdated, newDietiUser);
        assertEquals(new DietiUser("Mariano", "Rossi", "example@mail.com", "Extended biography", "Napoli" , null), toBeUpdated);
    }

    @Test
    void updateData_covers_2_1() {
        assertThrows(NullPointerException.class, () -> dietiUserService.updateData(null, newDietiUser));
    }

    @Test
    void updateData_covers_1_2() {
        assertThrows(NullPointerException.class, () -> dietiUserService.updateData(toBeUpdated, null));
    }

    @Test
    void updateData_covers_2_2() {
        assertThrows(NullPointerException.class, () -> dietiUserService.updateData(null, null));
    }
}