package src.main.java.ru.tbank.zaedu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import src.main.java.ru.tbank.zaedu.DTO.AuthenticationRequest;
import src.main.java.ru.tbank.zaedu.DTO.AuthenticationResponse;
import src.main.java.ru.tbank.zaedu.DTO.RegistrationClientRequest;
import src.main.java.ru.tbank.zaedu.DTO.RegistrationMasterRequest;
import src.main.java.ru.tbank.zaedu.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register/client")
    public ResponseEntity<AuthenticationResponse> registerClient(@RequestBody RegistrationClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.registerClient(request));
    }

    @PostMapping("/register/master")
    public ResponseEntity<AuthenticationResponse> registerMaster(@RequestBody RegistrationMasterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.registerMaster(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
