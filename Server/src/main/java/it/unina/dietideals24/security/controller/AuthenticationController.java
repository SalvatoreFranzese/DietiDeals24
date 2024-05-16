package it.unina.dietideals24.security.controller;

import it.unina.dietideals24.model.DietiUser;
import it.unina.dietideals24.repository.IDietiUserRepository;
import it.unina.dietideals24.security.dto.LoginDto;
import it.unina.dietideals24.security.dto.RegisterDto;
import it.unina.dietideals24.security.response.LoginResponse;
import it.unina.dietideals24.security.service.AuthenticationService;
import it.unina.dietideals24.security.service.JwtService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;
    private final IDietiUserRepository dietiUserRepository;
    private final JwtService jwtService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, AuthenticationManager authenticationManager, IDietiUserRepository dietiUserRepository, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
        this.dietiUserRepository = dietiUserRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<DietiUser> register(@RequestBody RegisterDto registerDTO) throws BadRequestException {
        if (dietiUserRepository.existsByEmail(registerDTO.getEmail()))
            throw new BadRequestException("Email already registered");

        DietiUser registeredUser = authenticationService.register(registerDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginDto loginDto) {
        DietiUser authenticatedUser = authenticationService.login(loginDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setDietiUser(authenticatedUser);

        return ResponseEntity.ok(loginResponse);
    }


}
