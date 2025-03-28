package ru.tbank.zaedu.service;

import ru.tbank.zaedu.DTO.AuthenticationRequest;
import ru.tbank.zaedu.DTO.AuthenticationResponse;
import ru.tbank.zaedu.DTO.RegistrationClientRequest;
import ru.tbank.zaedu.DTO.RegistrationMasterRequest;

public interface AuthenticationService {

    AuthenticationResponse registerClient(RegistrationClientRequest request);

    AuthenticationResponse registerMaster(RegistrationMasterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);
}
