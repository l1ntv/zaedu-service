package ru.tbank.zaedu.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@AllArgsConstructor
@Getter
@Setter
public class ReportDTO {
    private Integer id;
    private String text;
    private Integer rating;
    private LocalDate date;
}