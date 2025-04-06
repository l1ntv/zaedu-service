package ru.tbank.zaedu.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tbank.zaedu.exceptionhandler.file.ResourceNotFoundException;
import ru.tbank.zaedu.models.MasterPortfolioImage;
import ru.tbank.zaedu.repo.MasterPortfolioImageRepository;
import ru.tbank.zaedu.s3storage.S3File;
import ru.tbank.zaedu.service.file.FileService;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final FileService fileService;

    private final MasterPortfolioImageRepository masterPortfolioImageRepository;

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
}
