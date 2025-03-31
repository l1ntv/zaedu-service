package src.main.java.ru.tbank.zaedu.DTO;

import java.time.LocalDate;
import lombok.Data;

@Data
public class PlacedOrdersByClientsResponse {
    private Long id;
    private ServiceDTO serviceDTO;
    private String clientName;
    private String address;
    private Long price;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
