package it.unina.dietideals24.controller;

import it.unina.dietideals24.dto.UpdatePasswordDto;
import it.unina.dietideals24.model.DietiUser;
import it.unina.dietideals24.service.interfaces.IDietiUserService;
import it.unina.dietideals24.service.interfaces.IImageService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
public class DietiUserController {

    private static final String PROFILE_PIC_DIRECTORY = "user";
    @Qualifier("mainDietiUserService")
    private final IDietiUserService dietiUserService;
    @Qualifier("locallyStoreImageService")
    private final IImageService imageService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DietiUserController(IDietiUserService dietiUserService, IImageService imageService, PasswordEncoder passwordEncoder) {
        this.dietiUserService = dietiUserService;
        this.imageService = imageService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("{id}")
    public DietiUser getUserById(@PathVariable("id") Long id) {
        return dietiUserService.getUserById(id);
    }

    @GetMapping("/email/{email}")
    public DietiUser getUserByEmail(@PathVariable("email") String email) {
        return dietiUserService.getUserByEmail(email);
    }

    @PostMapping("{id}/profile-picture")
    public DietiUser updateProfilePicture(@PathVariable("id") Long id, @RequestParam("image") MultipartFile image) throws BadRequestException {
        if (dietiUserService.existsById(id)) {
            try {
                imageService.saveImage(PROFILE_PIC_DIRECTORY, id, image);
                dietiUserService.linkImage(PROFILE_PIC_DIRECTORY, id);
                return dietiUserService.getUserById(id);
            } catch (IOException e) {
                throw new BadRequestException("Image link failed!");
            }
        } else
            throw new BadRequestException("User not found!");
    }

    @PostMapping("{id}")
    public DietiUser updateDietiUserDataById(@PathVariable("id") Long id, @RequestBody DietiUser newDietiUser) throws BadRequestException {
        DietiUser toBeUpdated = dietiUserService.getUserById(id);
        if (toBeUpdated == null)
            throw new BadRequestException("User not found");

        return dietiUserService.updateDietiUserData(toBeUpdated, newDietiUser);
    }

    @PostMapping("{id}/password")
    public DietiUser updatePassword(@PathVariable("id") Long id, @RequestBody UpdatePasswordDto updatePasswordDto) throws BadRequestException {
        DietiUser toBeUpdated = dietiUserService.getUserById(id);
        if (toBeUpdated == null)
            throw new BadRequestException("User not found");

        String newPassword = updatePasswordDto.getNewPassword();
        String confirmOldPassword = updatePasswordDto.getOldPassword();
        String oldPassword = toBeUpdated.getPassword();

        if (passwordEncoder.matches(confirmOldPassword, oldPassword)) {
            return dietiUserService.updateDietiUserPassword(toBeUpdated, passwordEncoder.encode(newPassword));
        } else {
            throw new BadRequestException("Passwords don't match");
        }
    }
}
