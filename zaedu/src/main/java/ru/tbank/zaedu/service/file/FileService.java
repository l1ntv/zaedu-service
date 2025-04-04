package ru.tbank.zaedu.service.file;

import ru.tbank.zaedu.s3storage.S3File;

public interface FileService {
    void save(S3File file);

    S3File get(String filename);

    void delete(String filename);
}
