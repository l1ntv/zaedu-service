package src.main.java.ru.tbank.zaedu.DTO;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ReportDTO {
    private Integer id;
    private String text;
    private Integer rating;
    private LocalDate date;
}
