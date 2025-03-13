package ru.tbank.zaedu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tbank.zaedu.config.JwtService;
import ru.tbank.zaedu.DTO.AuthenticationRequest;
import ru.tbank.zaedu.DTO.AuthenticationResponse;
import ru.tbank.zaedu.DTO.RegistrationClientRequest;
import ru.tbank.zaedu.DTO.RegistrationMasterRequest;
import ru.tbank.zaedu.models.*;
import ru.tbank.zaedu.repo.ClientProfileRepository;
import ru.tbank.zaedu.repo.MasterProfileRepository;
import ru.tbank.zaedu.repo.UserRepository;
import ru.tbank.zaedu.service.AuthenticationService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;

    private final ClientProfileRepository clientProfileRepository;

    private final MasterProfileRepository masterProfileRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse registerClient(RegistrationClientRequest request) {
        var user = User.builder()
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(new UserRole(1L, "CLIENT"))
                .status(new UserStatus(1L, "ONLINE"))
                .build();
        userRepository.save(user);

        var clientProfile = ClientProfile.builder()
                .user(user)
                .build();
        clientProfileRepository.save(clientProfile);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponse registerMaster(RegistrationMasterRequest request) {
        var user = User.builder()
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(new UserRole(2L, "MASTER"))
                .status(new UserStatus(1L, "ONLINE"))
                .build();
        userRepository.save(user);

        var masterProfile = MasterProfile.builder()
                .surname(request.getSurname())
                .name(request.getName())
                .patronymic(request.getPatronymic())
                .isCompany(request.getIsCompany())
                .profileName(request.getProfileName())
                .build();
        masterProfileRepository.save(masterProfile);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        return null;
    }
}
