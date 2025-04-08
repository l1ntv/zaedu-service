package ru.tbank.zaedu.service;

import jakarta.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.tbank.zaedu.DTO.*;
import ru.tbank.zaedu.exceptionhandler.ConflictResourceException;
import ru.tbank.zaedu.exceptionhandler.ResourceNotFoundException;
import ru.tbank.zaedu.exceptionhandler.InvalidDataException;
import ru.tbank.zaedu.models.*;
import ru.tbank.zaedu.repo.HoodRepository;
import ru.tbank.zaedu.repo.MasterMainImageRepository;
import ru.tbank.zaedu.repo.MasterProfileRepository;
import ru.tbank.zaedu.repo.ServiceRepository;
import ru.tbank.zaedu.service.file.FileService;

@Service
@RequiredArgsConstructor
public class MasterServiceImpl implements MasterService {

    private final MasterProfileRepository masterProfileRepository;
    private final MasterMainImageRepository masterMainImageRepository;
    private final ServiceRepository serviceRepository;
    private final HoodRepository hoodRepository;
    private final FileService fileService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public List<MasterProfile> searchMastersByCategory(String category) {
        return masterProfileRepository.findByServiceCategory(category);
    }

    @Override
    public MasterProfile getMasterProfile(Long masterId) {
        return masterProfileRepository
                .findById(masterId)
                .orElseThrow(() -> new ResourceNotFoundException("Master not found"));
    }

    @Override
    public MasterProfile getMyPublicProfile(Principal principal) {
        String masterLogin = principal.getName();
        return masterProfileRepository
                .findByUser_Login(masterLogin)
                .orElseThrow(() -> new ResourceNotFoundException("Master not found for login: " + masterLogin));
    }

    @Override
    public MasterProfile getMyPrivateProfile(Principal principal) {
        String masterLogin = principal.getName();
        return masterProfileRepository
                .findByUser_Login(masterLogin)
                .orElseThrow(() -> new ResourceNotFoundException("Master not found for login: " + masterLogin));
    }

    @Override
    @Transactional
    public void updateMasterProfile(Principal principal, MasterUpdateRequestDTO request) {
        String masterLogin = principal.getName();
        MasterProfile master = masterProfileRepository
                .findByUser_Login(masterLogin)
                .orElseThrow(() -> new ResourceNotFoundException("Master not found for login: " + masterLogin));

        // Обновление основных полей
        master.setDescription(request.getDescription());

        // Список новых услуг из запроса
        List<MasterServiceEntity> updatedServices = new ArrayList<>();

        for (ServiceDTO dto : request.getServices()) {
            Services service = serviceRepository
                    .findByName(dto.getServiceName())
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

            // Проверяем, существует ли уже такой сервис у мастера
            List<MasterServiceEntity> services = master.getServices(); // Получаем список услуг
            if (services == null) {
                services = new ArrayList<>(); // Инициализируем пустой список, если он null
            }
            Optional<MasterServiceEntity> existingService = services.stream()
                    .filter(ms -> ms.getServices().getName().equals(dto.getServiceName()))
                    .findFirst();

            if (existingService.isPresent()) {
                // Обновляем цену существующего сервиса
                MasterServiceEntity serviceToUpdate = existingService.get();
                serviceToUpdate.setPrice(dto.getCost());
                updatedServices.add(serviceToUpdate);
            } else {
                // Создаем новый сервис
                MasterServiceEntity newService = new MasterServiceEntity()
                        .setMaster(master)
                        .setServices(service)
                        .setPrice(dto.getCost());
                updatedServices.add(newService);
            }
        }

        // Обновляем список услуг мастера
        // Удаляем только те услуги, которые больше не переданы в запросе
        if (master.getServices() != null) {
            master.getServices()
                    .removeIf(existingService -> updatedServices.stream().noneMatch(updatedService -> updatedService
                            .getServices()
                            .getName()
                            .equals(existingService.getServices().getName())));
        }

        // Добавляем обновленные и новые услуги
        master.getServices().addAll(updatedServices);

        // Обновление районов
        List<Hood> hoods = request.getDistricts().stream()
                .map(district -> hoodRepository
                        .findByName(district)
                        .orElseThrow(() -> new ResourceNotFoundException("Hood not found")))
                .collect(Collectors.toList());

        // Проверяем существующие связи и добавляем только новые
        for (Hood hood : hoods) {
            if (!master.getHoods().stream().anyMatch(mh -> mh.getHood().equals(hood))) {
                master.addHood(hood);
            }
        }

        // Обновление портфолио
        if (request.getPhotos() != null) {
            int totalPhotos = master.getPortfolioImages().size() + request.getPhotos().size();

            if (totalPhotos > 10) {
                throw new InvalidDataException("Portfolio cannot contain more than 10 photos");
            }

            List<MasterPortfolioImage> newPortfolio = request.getPhotos().stream()
                    .map(photo -> {
                        boolean alreadyExists = master.getPortfolioImages().stream()
                                .anyMatch(img -> img.getUploadId().equals(photo.getUuid()));

                        if (alreadyExists) {
                            throw new ConflictResourceException(
                                    "Photo with UUID " + photo.getUuid() + " already exists in portfolio");
                        }

                        return new MasterPortfolioImage()
                                .setMaster(master)
                                .setUploadId(photo.getUuid())
                                .setFilename(photo.getFilename());
                    })
                    .toList();

            //master.getPortfolioImages().addAll(newPortfolio);
        }

        // Обновление основного изображения профиля
        if (request.getFilename() != null && request.getUuid() != null
                && Objects.isNull(master.getMainImage())) {

            MasterMainImage masterMainImage = new MasterMainImage
                    (request.getUuid(), master, request.getFilename());

            masterMainImageRepository.save(masterMainImage);
            master.setMainImage(masterMainImage);
        } else if (request.getFilename() != null && request.getUuid() != null) {

            fileService.delete(master.getMainImage().getFilename());
            master.getMainImage().setFilename(request.getFilename());
            master.getMainImage().setUploadId(request.getUuid());
        } else if (request.getFilename() == null && request.getUuid() == null
                && Objects.nonNull(master.getMainImage())) {

            fileService.delete(master.getMainImage().getFilename());
            master.setMainImage(null);
        }

        masterProfileRepository.save(master);
    }

    @Override
    @Transactional
    public void updatePrivateProfile(Principal principal, MasterPrivateProfileUpdateRequestDTO request) {
        String masterLogin = principal.getName();
        MasterProfile master = masterProfileRepository
                .findByUser_Login(masterLogin)
                .orElseThrow(() -> new ResourceNotFoundException("Master not found for login: " + masterLogin));

        // Обновление полей приватного профиля
        master.setSurname(request.getSurname());
        master.setName(request.getName());
        master.setPatronymic(request.getPatronymic());
        master.setEmail(request.getEmail());
        master.setTelephoneNumber(request.getTelephoneNumber());
        master.setIsCompany(request.getIsCompany());
        master.setPassportSeries(request.getPassportSeries());
        master.setPassportNumber(request.getPassportNumber());

        masterProfileRepository.save(master);

        // Отправка данных в Kafka
        String passportKey = request.getPassportSeries() + ":" + request.getPassportNumber();
        kafkaTemplate.send("passport-validation-request", passportKey);
    }
}
