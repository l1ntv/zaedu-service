package ru.tbank.zaedu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tbank.zaedu.DTO.*;
import ru.tbank.zaedu.config.JwtService;
import ru.tbank.zaedu.exceptionhandler.InvalidDataException;
import ru.tbank.zaedu.exceptionhandler.ResourceNotFoundException;
import ru.tbank.zaedu.exceptionhandler.WrongDataException;
import ru.tbank.zaedu.models.*;
import ru.tbank.zaedu.repo.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    private final ClientProfileRepository clientProfileRepository;

    private final MasterProfileRepository masterProfileRepository;

    private final UserRoleRepository userRoleRepository;

    private final UserStatusRepository userStatusRepository;

    private final MasterServiceEntityRepository masterServiceEntityRepository;

    private final ServiceRepository serviceRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private static final String ROLE_CUSTOMER_CONST = "ROLE_CUSTOMER";

    private static final String ROLE_EXECUTOR_CONST = "ROLE_EXECUTOR";

    private static final String ONLINE_STATUS_CONST = "ONLINE";

    @Override
    public AuthenticationResponse registerClient(RegistrationClientRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new InvalidDataException("LoginAlreadyExists");
        }


        var clientRole = userRoleRepository.findByName(AuthenticationServiceImpl.ROLE_CUSTOMER_CONST).orElseThrow(() -> new ResourceNotFoundException("NotFoundUserRole"));
        var onlineStatus = userStatusRepository.findByName(AuthenticationServiceImpl.ONLINE_STATUS_CONST).orElseThrow(() -> new ResourceNotFoundException("NotFoundOnlineStatus"));

        var user = User.builder()
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(clientRole)
                .status(onlineStatus)
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
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new InvalidDataException("LoginAlreadyExists");
        }

        if (request.getServices() == null && request.getServices().isEmpty()) {
            throw new InvalidDataException("IncorrectServicesParameter");
        }

        for (ServiceDTO service : request.getServices()) {
            Services existingService = serviceRepository.findByName(service.getServiceName()).orElseThrow(() -> new ResourceNotFoundException("NotFoundService"));
        }

            var clientRole = userRoleRepository.findByName(AuthenticationServiceImpl.ROLE_EXECUTOR_CONST).orElseThrow(() -> new ResourceNotFoundException("NotFoundUserRole"));
        var onlineStatus = userStatusRepository.findByName(AuthenticationServiceImpl.ONLINE_STATUS_CONST).orElseThrow(() -> new ResourceNotFoundException("NotFoundOnlineStatus"));

        var user = User.builder()
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(clientRole)
                .status(onlineStatus)
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

        for (ServiceDTO service : request.getServices()) {
            Services existingService = serviceRepository.findByName(service.getServiceName()).orElseThrow(() -> new ResourceNotFoundException("NotFoundService"));
            MasterServiceEntity masterServiceEntity = new MasterServiceEntity();
            masterServiceEntity.setMaster(masterProfile);
            masterServiceEntity.setServices(existingService);
            masterServiceEntity.setPrice(service.getCost());
            masterServiceEntityRepository.save(masterServiceEntity);
        }


        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new InvalidDataException("NotFoundUser"));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new WrongDataException("WrongAuthData");
        }

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}