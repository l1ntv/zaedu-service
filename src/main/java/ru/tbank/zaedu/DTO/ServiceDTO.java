package ru.tbank.zaedu.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ServiceDTO {
    private Long id;
    private String serviceName;
    private Long cost;

    @JsonCreator
    public ServiceDTO(@JsonProperty("serviceName") String serviceName, @JsonProperty("cost") Long cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public ServiceDTO(long i, String serviceName) {
        this.id = i;
        this.serviceName = serviceName;
        this.cost = null;
    }
}
