package it.unina.dietideals24.security.service;

import it.unina.dietideals24.model.DietiUser;
import it.unina.dietideals24.repository.IDietiUserRepository;
import it.unina.dietideals24.security.dto.LoginDto;
import it.unina.dietideals24.security.dto.RegisterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final IDietiUserRepository dietiUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(IDietiUserRepository dietiUserRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.dietiUserRepository = dietiUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public DietiUser register(RegisterDto registerDTO) {
        DietiUser dietiUser = new DietiUser();
        dietiUser.setEmail(registerDTO.getEmail());
        dietiUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        dietiUser.setName(registerDTO.getName());
        dietiUser.setSurname(registerDTO.getSurname());

        return dietiUserRepository.save(dietiUser);
    }

    public DietiUser login(LoginDto loginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        return dietiUserRepository.findByEmail(loginDto.getEmail())
                .orElseThrow();
    }
}
