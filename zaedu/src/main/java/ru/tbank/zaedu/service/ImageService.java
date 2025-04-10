package ru.tbank.zaedu.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tbank.zaedu.exceptionhandler.file.ResourceNotFoundException;
import ru.tbank.zaedu.models.ClientMainImage;
import ru.tbank.zaedu.models.MasterMainImage;
import ru.tbank.zaedu.models.MasterPortfolioImage;
import ru.tbank.zaedu.repo.ClientMainImageRepository;
import ru.tbank.zaedu.repo.MasterMainImageRepository;
import ru.tbank.zaedu.repo.MasterPortfolioImageRepository;
import ru.tbank.zaedu.s3storage.S3File;
import ru.tbank.zaedu.service.file.FileService;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final FileService fileService;

    private final MasterPortfolioImageRepository masterPortfolioImageRepository;

    private final ClientMainImageRepository clientMainImageRepository;

    private final MasterMainImageRepository masterMainImageRepository;

    @SneakyThrows
    public S3File upload(MultipartFile file) {
        S3File s3File = new S3File(file.getOriginalFilename(), file.getBytes());
        fileService.save(s3File);
        return s3File;
    }

    public void delete(String filename) {
        fileService.delete(filename);
        MasterPortfolioImage masterPortfolioImageForDelete = masterPortfolioImageRepository.findByFilename(filename).orElseThrow(ResourceNotFoundException::new);
        masterPortfolioImageRepository.delete(masterPortfolioImageForDelete);
    }

    public S3File getMediaFileByUuid(UUID uuid) {
        Optional<ClientMainImage> clientMainImage = clientMainImageRepository.findByUploadId(uuid);
        if (clientMainImage.isPresent()) {
            return fileService.get(clientMainImage.get().getFilename());
        }

        Optional<MasterMainImage> masterImage = masterMainImageRepository.findByUploadId(uuid);
        if (masterImage.isPresent()) {
            return fileService.get(masterImage.get().getFilename());
        }

        Optional<MasterPortfolioImage> masterPortfolioImage = masterPortfolioImageRepository.findByUploadId(uuid);
        if (masterPortfolioImage.isPresent()) {
            return fileService.get(masterPortfolioImage.get().getFilename());
        }

        throw new ResourceNotFoundException("Image not found");
    }
}
