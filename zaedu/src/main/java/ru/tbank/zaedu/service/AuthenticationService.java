package src.main.java.ru.tbank.zaedu.service;

import src.main.java.ru.tbank.zaedu.DTO.AuthenticationRequest;
import src.main.java.ru.tbank.zaedu.DTO.AuthenticationResponse;
import src.main.java.ru.tbank.zaedu.DTO.RegistrationClientRequest;
import src.main.java.ru.tbank.zaedu.DTO.RegistrationMasterRequest;

public interface AuthenticationService {

    AuthenticationResponse registerClient(RegistrationClientRequest request);

    AuthenticationResponse registerMaster(RegistrationMasterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);
}
