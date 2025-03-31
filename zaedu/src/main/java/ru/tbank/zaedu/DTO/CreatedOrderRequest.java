package src.main.java.ru.tbank.zaedu.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreatedOrderRequest {
    private String serviceName;
    private String description;
    private Long price;
    private String address;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
