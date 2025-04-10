package ru.tbank.zaedu.masters_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.zaedu.AbstractIntegrationTest;
import ru.tbank.zaedu.DTO.*;
import ru.tbank.zaedu.models.*;
import ru.tbank.zaedu.repo.*;
import ru.tbank.zaedu.service.ClientService;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ClientServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientProfileRepository clientProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        clientProfileRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testGetMyProfile_Success() {
        // Arrange
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");
        userRepository.save(user);

        ClientProfile mockProfile = new ClientProfile();
        mockProfile.setUser(user);
        mockProfile.setEmail("testuser@mail.ru");
        clientProfileRepository.save(mockProfile);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testuser");

        // Act
        ClientProfile result = clientService.getClientProfileByName(principal.getName());

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUser().getLogin());
        assertEquals("testuser@mail.ru", result.getEmail());
    }

    @Test
    @Transactional
    void testUpdateClientProfile_Success() {
        // Arrange
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");
        userRepository.save(user);
        System.out.println("Saved user: " + user);

        ClientProfile mockProfile = new ClientProfile();
        mockProfile.setUser(user);
        mockProfile.setEmail("oldEmail@mail.ru");
        mockProfile.setName("oldName");
        clientProfileRepository.save(mockProfile);
        System.out.println("Saved profile: " + mockProfile);

        FileResponseDto fileResponseDto = new FileResponseDto();
        fileResponseDto.setUuid(UUID.randomUUID());
        fileResponseDto.setFilename("photo.jpg");

        ClientProfileRequestDTO request = new ClientProfileRequestDTO();
        request.setEmail("newEmail@mail.ru");
        request.setName("newName");
        request.setFilename(fileResponseDto.getFilename());
        request.setUuid(fileResponseDto.getUuid());

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testuser");

        // Act
        clientService.updateClientProfile(principal.getName(), request);

        // Assert
        ClientProfile updatedProfile = clientProfileRepository.findByUser(user).orElseThrow();
        assertEquals("newEmail@mail.ru", updatedProfile.getEmail());
        assertEquals("newName", updatedProfile.getName());

        assertEquals(fileResponseDto.getUuid(), updatedProfile.getMainImage().getUploadId());
        assertEquals(fileResponseDto.getFilename(), updatedProfile.getMainImage().getFilename());
    }

    @Test
    void testGetClientProfile_Success() {
        // Arrange
        User user = new User();
        user.setLogin("testuser");
        userRepository.save(user);

        ClientProfile mockProfile = new ClientProfile();
        mockProfile.setUser(user);
        mockProfile.setEmail("testuser@mail.ru");
        clientProfileRepository.save(mockProfile);

        // Act
        Optional<ClientProfile> result = clientService.getClientProfileById(mockProfile.getId());

        // Assert
        assertNotNull(result);
        assertEquals("testuser@mail.ru", result.get().getEmail());
    }

    @Test
    void testGetClientProfile_NotFound() {
        // Act & Assert
        Optional<ClientProfile> result = clientService.getClientProfileById(999L);

        assertTrue(result.isEmpty(), "Client not found");
    }
}
