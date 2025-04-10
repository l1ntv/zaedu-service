package ru.tbank.zaedu.controller;

import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tbank.zaedu.DTO.FileResponseDto;
import ru.tbank.zaedu.s3storage.S3File;
import ru.tbank.zaedu.service.ImageService;

import java.util.UUID;

@RestController
@RequestMapping("/images")
@AllArgsConstructor
public class FileController {

    private final ImageService imageService;

    private final ModelMapper modelMapper;

    private static final Class<FileResponseDto> FILE_RESPONSE_DTO_CLASS = FileResponseDto.class;

    @PostMapping(value = "/upload")
    public ResponseEntity<Object> uploadImage(MultipartFile file) {
        S3File s3File = imageService.upload(file);
        return ResponseEntity.ok(modelMapper.map(s3File, FILE_RESPONSE_DTO_CLASS));
    }

    @PostMapping(value = "/delete")
    public void deleteImage(@RequestParam String filename) {
        imageService.delete(filename);
    }

    @GetMapping(value = "/get/{uuid}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<String> getImageByUuid(@PathVariable UUID uuid) {
        S3File image = imageService.getMediaFileByUuid(uuid);

        String base64Image = Base64.encodeBase64String(image.getContent());

        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(base64Image);
    }
}
