package ru.tbank.zaedu.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class FileResponseDto {
    private String filename;
    private UUID uuid;
}
