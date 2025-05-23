package ru.tbank.zaedu.masters_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.zaedu.AbstractIntegrationTest;
import ru.tbank.zaedu.DTO.*;
import ru.tbank.zaedu.models.*;
import ru.tbank.zaedu.repo.*;
import ru.tbank.zaedu.exceptionhandler.ResourceNotFoundException;
import ru.tbank.zaedu.service.MasterServiceImpl;
import ru.tbank.zaedu.service.OrderServiceImpl;
import ru.tbank.zaedu.service.file.FileService;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MasterServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MasterServiceImpl masterService;


    @Autowired
    private MasterProfileRepository masterProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MasterHoodsRepository masterHoodsRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private HoodRepository hoodRepository;




    @MockitoBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockitoBean
    private FileService fileervice;

    @BeforeEach
    void setUp() {
        masterProfileRepository.deleteAll();
        userRepository.deleteAll();
        serviceRepository.deleteAll();
        hoodRepository.deleteAll();
        masterHoodsRepository.deleteAll();
    }

    @Test
    void testGetMyPublicProfile_Success() {
        // Arrange
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password"); // Установите пароль или другие обязательные поля
        userRepository.save(user); // Сохраняем пользователя в базу данных

        MasterProfile mockProfile = new MasterProfile();
        mockProfile.setUser(user); // Связываем профиль с пользователем
        mockProfile.setDescription("Test description");
        masterProfileRepository.save(mockProfile);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testuser");

        // Act
        MasterProfile result = masterService.getMyPublicProfile(principal);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUser().getLogin());
        assertEquals("Test description", result.getDescription());
    }

    @Test
    @Transactional
    void testUpdateMasterProfile_Success() {
        // Arrange
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");
        userRepository.save(user);

        MasterProfile mockProfile = new MasterProfile();
        mockProfile.setUser(user);
        mockProfile.setDescription("Old description");
        masterProfileRepository.save(mockProfile);

        // Создаем моковые данные для ServiceDTO
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setServiceName("Test Service");
        serviceDTO.setCost(100L);


        // Создаем моковые данные для Hood
        Hood hood = new Hood();
        hood.setName("Test District");
        hoodRepository.save(hood); // Сохраняем район в базу данных


        // Создаем моковые данные для FileResponseDto
        FileResponseDto fileResponseDto = new FileResponseDto();
        fileResponseDto.setUuid(UUID.randomUUID());
        fileResponseDto.setFilename("photo.jpg");

        FileResponseDto fileResponseDto1 = new FileResponseDto();
        fileResponseDto1.setUuid(UUID.randomUUID());
        fileResponseDto1.setFilename("photo1.jpg");

        FileResponseDto fileResponseDto2 = new FileResponseDto();
        fileResponseDto2.setUuid(UUID.randomUUID());
        fileResponseDto2.setFilename("photo2.jpg");



        // Создаем DTO для обновления профиля
        MasterUpdateRequestDTO request = new MasterUpdateRequestDTO();
        request.setDescription("New description");
        request.setServices(List.of(serviceDTO));
        request.setDistricts(List.of("Test District")); // Используем имя района
        request.setPhotos(List.of(fileResponseDto, fileResponseDto1, fileResponseDto2));

        // Мокируем Principal
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testuser");

        // Создаем и сохраняем Services
        Services mockService = new Services();
        mockService.setName("Test Service");
        serviceRepository.save(mockService); // Сохраняем сервис в базу данных


        // Act
        masterService.updateMasterProfile(principal, request);

        // Assert
        MasterProfile updatedProfile = masterProfileRepository.findByUser_Login("testuser").orElseThrow();
        assertEquals("New description", updatedProfile.getDescription());

        // Проверяем, что услуги были обновлены
        assertEquals(1, updatedProfile.getServices().size());
        assertEquals("Test Service", updatedProfile.getServices().get(0).getServices().getName());
        assertEquals(100, updatedProfile.getServices().get(0).getPrice());

        // Проверяем, что районы были добавлены
        assertEquals(1, updatedProfile.getHoods().size());
        assertEquals("Test District", updatedProfile.getHoods().get(0).getHood().getName());

        // Проверяем, что фото было добавлено
        assertEquals(3, updatedProfile.getPortfolioImages().size());
        assertEquals(fileResponseDto.getUuid(), updatedProfile.getPortfolioImages().get(0).getUploadId());
        assertEquals(fileResponseDto.getFilename(), updatedProfile.getPortfolioImages().get(0).getFilename());
    }

    @Test
    void testUpdatePrivateProfile_Success() {
        // Arrange
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password"); // Установите пароль или другие обязательные поля
        userRepository.save(user); // Сохраняем пользователя в базу данных

        MasterProfile mockProfile = new MasterProfile();
        mockProfile.setUser(user); // Связываем профиль с пользователем
        mockProfile.setSurname("Old Surname");
        masterProfileRepository.save(mockProfile);

        MasterPrivateProfileUpdateRequestDTO request = new MasterPrivateProfileUpdateRequestDTO();
        request.setSurname("New Surname");
        request.setEmail("new.email@example.com");

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testuser");

        // Act
        masterService.updatePrivateProfile(principal, request);

        // Assert
        MasterProfile updatedProfile = masterProfileRepository.findByUser_Login("testuser").orElseThrow();
        assertEquals("New Surname", updatedProfile.getSurname());
        assertEquals("new.email@example.com", updatedProfile.getEmail());

        verify(kafkaTemplate).send(eq("passport-validation-request"), anyString());
    }

    @Test
    void testSearchMastersByCategory_Success() {
        // Arrange
        User user = new User();
        user.setLogin("testuser");
        userRepository.save(user);

        MasterProfile masterProfile = new MasterProfile();
        masterProfile.setUser(user);
        masterProfile.setDescription("Test description");
        masterProfileRepository.save(masterProfile);

        Services service = new Services();
        service.setName("TestCategory");
        serviceRepository.save(service);

        MasterServiceEntity masterService = new MasterServiceEntity();
        masterService.setMaster(masterProfile);
        masterService.setServices(service);
        masterService.setPrice(100L);
        masterProfile.getServices().add(masterService);
        masterProfileRepository.save(masterProfile);

        // Act
        List<MasterProfile> result = masterProfileRepository.findByServiceCategory("TestCategory");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test description", result.get(0).getDescription());
    }

    @Test
    void testSearchMastersByCategory_EmptyResult() {
        // Act
        List<MasterProfile> result = masterService.searchMastersByCategory("NonExistentCategory");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetMasterProfile_Success() {
        // Arrange
        User user = new User();
        user.setLogin("testuser");
        userRepository.save(user);

        MasterProfile mockProfile = new MasterProfile();
        mockProfile.setUser(user);
        mockProfile.setDescription("Test description");
        masterProfileRepository.save(mockProfile);

        // Act
        MasterProfile result = masterService.getMasterProfile(mockProfile.getId());

        // Assert
        assertNotNull(result);
        assertEquals("Test description", result.getDescription());
    }

    @Test
    void testGetMasterProfile_NotFound() {
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            masterService.getMasterProfile(999L); // Не существующий ID
        });

        assertEquals("Master not found", exception.getMessage());
    }

    @Test
    void testGetMyPrivateProfile_Success() {
        // Arrange
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");
        userRepository.save(user);

        MasterProfile mockProfile = new MasterProfile();
        mockProfile.setUser(user);
        mockProfile.setSurname("Old Surname");
        mockProfile.setEmail("old.email@example.com");
        masterProfileRepository.save(mockProfile);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testuser");

        // Act
        MasterProfile result = masterService.getMyPrivateProfile(principal);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUser().getLogin());
        assertEquals("Old Surname", result.getSurname());
        assertEquals("old.email@example.com", result.getEmail());
    }

    @Test
    void testGetMyPrivateProfile_NotFound() {
        // Arrange
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("nonexistentuser");

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            masterService.getMyPrivateProfile(principal);
        });

        assertEquals("Master not found for login: nonexistentuser", exception.getMessage());
    }

}
