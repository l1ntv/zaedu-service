package src.main.java.ru.tbank.zaedu.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import src.main.java.ru.tbank.zaedu.DTO.*;
import src.main.java.ru.tbank.zaedu.config.JwtService;
import src.main.java.ru.tbank.zaedu.enums.UserRoleEnum;
import src.main.java.ru.tbank.zaedu.enums.UserStatusEnum;
import src.main.java.ru.tbank.zaedu.exceptionhandler.InvalidDataException;
import src.main.java.ru.tbank.zaedu.exceptionhandler.LockedUserException;
import src.main.java.ru.tbank.zaedu.exceptionhandler.ResourceNotFoundException;
import src.main.java.ru.tbank.zaedu.exceptionhandler.WrongDataException;
import src.main.java.ru.tbank.zaedu.models.*;
import src.main.java.ru.tbank.zaedu.repo.*;

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

    @Transactional
    @Override
    public AuthenticationResponse registerClient(RegistrationClientRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new InvalidDataException("LoginAlreadyExists");
        }

        var clientRole = this.findUserRoleByName(UserRoleEnum.ROLE_CUSTOMER.toString());
        var onlineStatus = this.findUserStatusByName(UserStatusEnum.ONLINE.toString());

        var user = User.builder()
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(clientRole)
                .status(onlineStatus)
                .build();
        userRepository.save(user);

        var clientProfile = ClientProfile.builder().user(user).build();
        clientProfileRepository.save(clientProfile);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    @Transactional
    @Override
    public AuthenticationResponse registerMaster(RegistrationMasterRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new InvalidDataException("LoginAlreadyExists");
        }

        this.correctnessServiceChecking(request.getServices());

        var clientRole = userRoleRepository
                .findByName(UserRoleEnum.ROLE_EXECUTOR.toString())
                .orElseThrow(() -> new ResourceNotFoundException("NotFoundUserRole"));
        var onlineStatus = userStatusRepository
                .findByName(UserStatusEnum.ONLINE.toString())
                .orElseThrow(() -> new ResourceNotFoundException("NotFoundOnlineStatus"));

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
                .user(user)
                .build();
        masterProfileRepository.save(masterProfile);

        for (ServiceDTO service : request.getServices()) {
            Services existingService = serviceRepository
                    .findByName(service.getServiceName())
                    .orElseThrow(() -> new ResourceNotFoundException("NotFoundService"));
            MasterServiceEntity masterServiceEntity = new MasterServiceEntity();
            masterServiceEntity.setMaster(masterProfile);
            masterServiceEntity.setServices(existingService);
            masterServiceEntity.setPrice(service.getCost());
            masterServiceEntityRepository.save(masterServiceEntity);
        }

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = this.findUserByLogin(request.getLogin());

        if (this.isUserBanned(user)) {
            throw new LockedUserException("UserBanned");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new WrongDataException("WrongAuthData");
        }

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    private User findUserByLogin(String login) throws ResourceNotFoundException {
        User user = userRepository.findByLogin(login).orElseThrow(() -> new ResourceNotFoundException("UserNotFound"));
        return user;
    }

    private UserRole findUserRoleByName(String name) throws ResourceNotFoundException {
        UserRole userRole = userRoleRepository
                .findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("UserRoleNotFound"));
        return userRole;
    }

    private UserStatus findUserStatusByName(String name) throws ResourceNotFoundException {
        UserStatus userStatus = userStatusRepository
                .findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("UserStatusNotFound"));
        return userStatus;
    }

    private void correctnessServiceChecking(List<ServiceDTO> services)
            throws InvalidDataException, ResourceNotFoundException {
        if (services == null && services.isEmpty()) {
            throw new InvalidDataException("IncorrectServicesParameter");
        }

        for (ServiceDTO service : services) {
            Services existingService = serviceRepository
                    .findByName(service.getServiceName())
                    .orElseThrow(() -> new ResourceNotFoundException("NotFoundService"));
        }
    }

    private boolean isUserBanned(User user) {
        return user.getStatus().getName().equals(UserStatusEnum.BLOCKED.toString());
    }
}
