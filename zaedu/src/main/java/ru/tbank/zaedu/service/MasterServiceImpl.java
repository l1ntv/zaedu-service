package src.main.java.ru.tbank.zaedu.service;

import jakarta.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import src.main.java.ru.tbank.zaedu.DTO.*;
import src.main.java.ru.tbank.zaedu.exceptionhandler.ConflictResourceException;
import src.main.java.ru.tbank.zaedu.exceptionhandler.ResourceNotFoundException;
import src.main.java.ru.tbank.zaedu.models.*;
import src.main.java.ru.tbank.zaedu.repo.HoodRepository;
import src.main.java.ru.tbank.zaedu.repo.MasterProfileRepository;
import src.main.java.ru.tbank.zaedu.repo.ServiceRepository;

@Service
@RequiredArgsConstructor
public class MasterServiceImpl implements MasterService {

    private final MasterProfileRepository masterProfileRepository;
    private final ServiceRepository serviceRepository;
    private final HoodRepository hoodRepository;
    private final ModelMapper modelMapper; // Добавьте ModelMapper

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
            Optional<MasterServiceEntity> existingService = master.getServices().stream()
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
        master.getServices()
                .removeIf(existingService -> updatedServices.stream().noneMatch(updatedService -> updatedService
                        .getServices()
                        .getName()
                        .equals(existingService.getServices().getName())));

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
        List<MasterPortfolioImage> portfolio = request.getPhotos().stream()
                .map(url -> {
                    // Проверка на дублирование ссылки на фото
                    if (master.getPortfolioImages().stream()
                            .anyMatch(image -> image.getUrl().equals(url))) {
                        throw new ConflictResourceException("Photo with URL " + url + " already exists in portfolio");
                    }
                    return new MasterPortfolioImage().setMaster(master).setUrl(url);
                })
                .collect(Collectors.toList());

        master.getPortfolioImages().addAll(portfolio); // Добавляем новые изображения

        // Обновление основного изображения профиля
        if (request.getPersonalPhoto() != null) {
            // Удаляем старые основные изображения, если они есть
            if (master.getMainImages() != null && !master.getMainImages().isEmpty()) {
                master.getMainImages().clear();
            }

            // Добавляем новое основное изображение
            MasterMainImage mainImage = new MasterMainImage().setMaster(master).setUrl(request.getPersonalPhoto());
            master.getMainImages().add(mainImage);
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
        master.setIsConfirmedPassport(request.getIsConfirmedPassport());
        master.setPassportSeries(request.getPassportSeries());
        master.setPassportNumber(request.getPassportNumber());

        masterProfileRepository.save(master);
    }
}
